package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class OverviewActivity extends AppCompatActivity {
    Fragment statusBarFragment, areaInfoFragment;
    MapAdapter mapAdapter;
    Button leaveButton;
    GameData gameData = GameData.getInstance();
    BroadcastReceiver mReceiver;

    private class MapVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView backgroundImage, areaStar, areaPlayer;
        ConstraintLayout constraintLayout;
        Area area;

        public MapVHolder(LayoutInflater li, ViewGroup parent) {
            super(li.inflate(R.layout.grid_cell, parent, false));
            itemView.setOnClickListener(this);
            int size = parent.getMeasuredHeight() / gameData.mapSize().y + 1;
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = size;
            lp.height = size;

            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            areaStar = itemView.findViewById(R.id.areaStar);
            areaPlayer = itemView.findViewById(R.id.areaPlayer);
            backgroundImage = itemView.findViewById(R.id.backgroundImage);
            constraintLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        public void bind(Area inArea) {
            // reset bg on bind
            constraintLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            area = inArea;
            areaStar.setVisibility(area.isStarred() ? View.VISIBLE : View.GONE);
            areaPlayer.setVisibility(area == gameData.getPlayerArea() ? View.VISIBLE : View.GONE);

            AreaInfoFragment areaInfo = (AreaInfoFragment) areaInfoFragment;
            if (areaInfo.getArea() == area) {
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            if (area.isExplored()) {
                backgroundImage.setImageResource(area.isTown() ? R.drawable.ic_building1 : R.drawable.ic_tree3);
            } else {
                backgroundImage.setImageResource(android.R.color.black);
            }
        }

        @Override
        public void onClick(View view) {
            AreaInfoFragment areaInfo = (AreaInfoFragment) areaInfoFragment;
            if (area.isExplored()) {
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                areaInfo.setAreaToUse(area);
                areaInfo.updateUI();
                gameData.broadcastUpdate();
            }
        }
    }

    private class MapAdapter extends RecyclerView.Adapter<MapVHolder> {
        public MapAdapter() {
        }

        @Override
        public int getItemCount() {
            Point size = gameData.mapSize();
            return size.x * size.y;
        }

        @Override
        public MapVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(OverviewActivity.this);
            return new MapVHolder(li, parent);
        }

        @Override
        public void onBindViewHolder(MapVHolder vh, int index) {
            int size = gameData.mapSize().y;
            int row = index / size;
            int col = index % size;
            vh.bind(gameData.getArea(new Point(row, col)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // load status bar
        FragmentManager fm = getSupportFragmentManager();
        statusBarFragment = fm.findFragmentById(R.id.statusBar);
        areaInfoFragment = fm.findFragmentById(R.id.area_info);

        FragmentTransaction ft = fm.beginTransaction();
        if (statusBarFragment == null) {
            statusBarFragment = new StatusBarFragment();
            ft.add(R.id.statusBar, statusBarFragment);
        }
        if (areaInfoFragment == null) {
            areaInfoFragment = new AreaInfoFragment();
            ft.add(R.id.area_info, areaInfoFragment);
        }
        ft.commit();

        leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NavigationActivity.newIntent(OverviewActivity.this);
                startActivity(intent);
            }
        });

        mReceiver = gameData.registerBroadcastReceiver(this, new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new GridLayoutManager(this,
                gameData.mapSize().x, GridLayoutManager.HORIZONTAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.HORIZONTAL));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

        mapAdapter = new MapAdapter();
        rv.setAdapter(mapAdapter);
    }

    private void updateUI() {
        mapAdapter.notifyDataSetChanged();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, OverviewActivity.class);
    }

    @Override
    public void onPause() {
        gameData.unregisterBroadcastReceiver(this, mReceiver);
        super.onPause();
    }
}
