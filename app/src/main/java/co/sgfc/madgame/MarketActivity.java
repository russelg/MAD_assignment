package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.List;
import java.util.Locale;

import co.sgfc.madgame.Items.ItemOwner;
import co.sgfc.madgame.Items.Usables.SmellOScope;

public class MarketActivity extends AppCompatActivity {
    private RecyclerView buyRecyclerView, sellRecyclerView;
    private ItemAdapter buyItemAdapter, sellItemAdapter;
    private Button leaveButton;
    private GameData gameData = GameData.getInstance();
    private BroadcastReceiver mReceiver;

    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemDetail;
        private Button buyButton, sellButton, useButton;
        private Item item;

        private void removeFrom(ItemManager source, Item item) {
            source.removeItem(item);
        }

        private void giveTo(ItemManager destination, Item item) {
            destination.addItem(item);
        }

        private void removeItem(Item item) {
            switch (item.getItemOwner()) {
                case MAP:
                    removeFrom(gameData.getPlayerArea(), item);
                    break;
                case PLAYER:
                    removeFrom(gameData.getPlayer(), item);
            }
        }

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));

            itemName = itemView.findViewById(R.id.itemName);
            itemDetail = itemView.findViewById(R.id.itemDetail);

            buyButton = itemView.findViewById(R.id.buyButton);
            sellButton = itemView.findViewById(R.id.sellButton);
            useButton = itemView.findViewById(R.id.useButton);

            buyButton.setOnClickListener(buyListener);
            sellButton.setOnClickListener(sellListener);
            useButton.setOnClickListener(useListener);
        }

        private void sendToast(String verb) {
            Toast.makeText(getApplicationContext(), getString(R.string.item_action, verb, item.getDescription()),
                    Toast.LENGTH_SHORT).show();
        }

        View.OnClickListener buyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTown = gameData.getPlayerArea().isTown();
                Player player = gameData.getPlayer();
                sendToast(getString(isTown ? R.string.item_bought : R.string.item_grabbed));

                if (isTown)
                    player.incrementCash(-item.getValue());

                removeFrom(gameData.getPlayerArea(), item);
                giveTo(gameData.getPlayer(), item);
                gameData.saveData();
            }
        };

        View.OnClickListener sellListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTown = gameData.getPlayerArea().isTown();
                Player player = gameData.getPlayer();
                sendToast(getString(isTown ? R.string.item_sold : R.string.item_dropped));

                if (isTown)
                    player.incrementCash(item.getValue() * 0.75);

                removeFrom(player, item);
                giveTo(gameData.getPlayerArea(), item);
                gameData.saveData();
            }
        };

        View.OnClickListener useListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player player = gameData.getPlayer();
                sendToast(getString(R.string.item_used));

                // pass in the context so an item can start an activity etc..
                item.use(MarketActivity.this);

                if (item instanceof Food
                        && gameData.getPlayerArea().isTown()
                        && item.getItemOwner() == ItemOwner.MAP) {
                    // charge player if the item is used from the market
                    player.incrementCash(-item.getValue());
                }

                removeItem(item);
                gameData.saveData();
            }
        };

        private void resetButtons() {
            buyButton.setEnabled(true);
            useButton.setEnabled(true);
            sellButton.setEnabled(true);

            buyButton.setVisibility(View.GONE);
            sellButton.setVisibility(View.GONE);
            useButton.setVisibility(View.GONE);

            buyButton.setText(R.string.item_buy);
            sellButton.setText(R.string.item_sell);
            useButton.setText(R.string.item_use);
        }

        private void updateButtons(Item item) {
            boolean inTown = gameData.getPlayerArea().isTown();
            boolean isFood = item instanceof Food;
            boolean isMapItem = item.getItemOwner() == ItemOwner.MAP;
            boolean canAfford = gameData.getPlayer().getCash() < item.getValue();

            if (!inTown) {
                // wilderness, no cash needed, just grab and drop items instead
                buyButton.setText(R.string.item_grab);
                sellButton.setText(R.string.item_drop);
            }

            if (isMapItem) {
                buyButton.setVisibility(View.VISIBLE);
            } else {
                sellButton.setVisibility(View.VISIBLE);
            }

            if (item.isUsable()) {
                useButton.setVisibility(View.VISIBLE);
            }

            if (isFood) {
                // food cannot be picked up
                buyButton.setVisibility(View.GONE);
            } else if (isMapItem) {
                // show the use button for only food items
                // and any items in inventory
                useButton.setVisibility(View.GONE);
            }

            if (inTown && isMapItem && canAfford) {
                // player cant afford item
                useButton.setEnabled(false);
                buyButton.setEnabled(false);
            }
        }

        public void bind(Item inItem) {
            item = inItem;
            itemName.setText(item.getDescription());
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.ENGLISH, "Value: $%.2f", item.getValue()));

            if (item instanceof Equipment)
                sb.append(String.format(Locale.ENGLISH, ", Mass: %.2fkg", ((Equipment) item).getMass()));
            if (item instanceof Food)
                sb.append(String.format(Locale.ENGLISH, ", Restores: %.2f HP", ((Food) item).getHealth()));

            itemDetail.setText(sb.toString());

            resetButtons();
            updateButtons(item);
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
            LayoutInflater layoutInflater = LayoutInflater.from(MarketActivity.this);
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
        setContentView(R.layout.activity_market);

        FragmentManager fm = getSupportFragmentManager();
        Fragment statusBarFragment = fm.findFragmentById(R.id.statusBar);
        FragmentTransaction ft = fm.beginTransaction();
        if (statusBarFragment == null) {
            statusBarFragment = new StatusBarFragment();
            ft.add(R.id.statusBar, statusBarFragment);
        }
        ft.commit();

        buyRecyclerView = findViewById(R.id.buyRecyclerView);
        sellRecyclerView = findViewById(R.id.sellRecyclerView);
        buyRecyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this, LinearLayoutManager.VERTICAL, false));
        sellRecyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this, LinearLayoutManager.VERTICAL, false));

        leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NavigationActivity.newIntent(MarketActivity.this));
                finish();
            }
        });

        mReceiver = gameData.registerBroadcastReceiver(this, new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

        buyItemAdapter = new ItemAdapter(gameData.getPlayerArea().getItems());
        sellItemAdapter = new ItemAdapter(gameData.getPlayer().getItems());
        buyRecyclerView.setAdapter(buyItemAdapter);
        sellRecyclerView.setAdapter(sellItemAdapter);
    }

    private void updateUI() {
        if (gameData.getPlayer().getGameStatus() != GameStatus.PLAYING) {
            startActivity(NavigationActivity.newIntent(MarketActivity.this));
            finish();
            return;
        }
        buyItemAdapter.setItems(gameData.getPlayerArea().getItems());
        sellItemAdapter.setItems(gameData.getPlayer().getItems());

        buyItemAdapter.notifyDataSetChanged();
        sellItemAdapter.notifyDataSetChanged();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MarketActivity.class);
    }

    @Override
    protected void onPause() {
        gameData.unregisterBroadcastReceiver(this, mReceiver);
        super.onPause();
    }
}
