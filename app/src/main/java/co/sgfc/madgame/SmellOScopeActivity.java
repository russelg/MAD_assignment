package co.sgfc.madgame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.sgfc.madgame.Items.ItemOwner;

public class SmellOScopeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private Button leaveButton;
    private GameData gameData = GameData.getInstance();

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemX, itemY;
        Item item;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_smell_item, parent, false));
            itemName = itemView.findViewById(R.id.itemName);
            itemX = itemView.findViewById(R.id.itemX);
            itemY = itemView.findViewById(R.id.itemY);
        }

        public void bind(Item inItem) {
            item = inItem;
            Point playerPos = gameData.getPlayer().getLocation();
            Point itemPos = item.getPosition();
            int diffY = itemPos.y - playerPos.y;
            int diffX = itemPos.x - playerPos.x;
            boolean itemIsNorth = diffY <= 0;
            boolean itemIsWest = diffX <= 0;
            itemName.setText(item.getDescription());
            itemX.setText(String.format(itemIsWest ? "%d west" : "%d east", Math.abs(diffX)));
            itemY.setText(String.format(itemIsNorth ? "%d north" : "%d south", Math.abs(diffY)));
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<Item> mItems;

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        public void setItems(List<Item> mItems) {
            this.mItems = mItems;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SmellOScopeActivity.this);
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smell_o_scope);

        FragmentManager fm = getSupportFragmentManager();
        Fragment statusBarFragment = fm.findFragmentById(R.id.statusBar);
        FragmentTransaction ft = fm.beginTransaction();
        if (statusBarFragment == null) {
            statusBarFragment = new StatusBarFragment();
            ft.add(R.id.statusBar, statusBarFragment);
        }
        ft.commit();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SmellOScopeActivity.this, LinearLayoutManager.VERTICAL, false));

        List<Area> areas = gameData.getPlayerSurroundingAreas(2, 2);
        List<Item> items = new ArrayList<>();
        for (Area area : areas) {
            items.addAll(area.getItems());
        }

        itemAdapter = new ItemAdapter(items);
        recyclerView.setAdapter(itemAdapter);

        leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MarketActivity.newIntent(SmellOScopeActivity.this));
                finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, SmellOScopeActivity.class);
    }
}
