package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.EnumSet;

public class NavigationActivity extends AppCompatActivity {
    // Navigation elements
    Button buttonNorth, buttonEast, buttonSouth, buttonWest,
            buttonAreaOptions, buttonOverview;
    ImageView areaBackground;

    // Game logic objects
    Player player;
    GameData gameData;

    BroadcastReceiver mReceiver;
    Fragment statusBarFragment, areaInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameData = GameData.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Navigation elements
        buttonNorth = findViewById(R.id.buttonNorth);
        buttonEast = findViewById(R.id.buttonEast);
        buttonSouth = findViewById(R.id.buttonSouth);
        buttonWest = findViewById(R.id.buttonWest);
        buttonAreaOptions = findViewById(R.id.buttonAreaOptions);
        buttonOverview = findViewById(R.id.buttonOverview);
        areaBackground = findViewById(R.id.areaBackground);

        gameData.getGameMap();
        player = gameData.getPlayer();

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

        buttonNorth.setOnClickListener(navigationListener);
        buttonEast.setOnClickListener(navigationListener);
        buttonSouth.setOnClickListener(navigationListener);
        buttonWest.setOnClickListener(navigationListener);
        buttonAreaOptions.setOnClickListener(areaOptionsListener);
        buttonOverview.setOnClickListener(areaOverviewListener);

        mReceiver = gameData.registerBroadcastReceiver(this, new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

        gameData.broadcastUpdate();
    }

    View.OnClickListener navigationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int spaces = 1;
            Direction direction = null;
            switch (v.getId()) {
                case R.id.buttonNorth:
                    direction = Direction.NORTH;
                    break;
                case R.id.buttonEast:
                    direction = Direction.EAST;
                    break;
                case R.id.buttonSouth:
                    direction = Direction.SOUTH;
                    break;
                case R.id.buttonWest:
                    direction = Direction.WEST;
                    break;
            }
            NavigationActivity.this.moveSpaces(direction, spaces);
        }
    };

    View.OnClickListener areaOptionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = MarketActivity.newIntent(NavigationActivity.this);
            startActivity(intent);
        }
    };

    View.OnClickListener areaOverviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = OverviewActivity.newIntent(NavigationActivity.this);
            startActivity(intent);
        }
    };

    public void moveSpaces(Direction direction, int spaces) {
        Point mapSize = gameData.mapSize();
        Point pos = player.getLocation();

        switch (direction) {
            case NORTH:
                if ((pos.y - spaces) >= 0)
                    pos.offset(0, -spaces);
                break;
            case SOUTH:
                if ((pos.y + spaces) < mapSize.y)
                    pos.offset(0, spaces);
                break;
            case EAST:
                if ((pos.x + spaces) < mapSize.x)
                    pos.offset(spaces, 0);
                break;
            case WEST:
                if ((pos.x - spaces) >= 0)
                    pos.offset(-spaces, 0);
                break;
        }

        double health = Math.max(0.0,
                player.getHealth() - 5.0 - (player.getEquipmentMass() / 2.0)) * spaces;
        player.setHealth(health);

        Area area = gameData.getArea(pos);
        player.setLocation(pos);
        area.setExplored(true);

        gameData.updatePlayer();
        gameData.updateArea(area);
        gameData.broadcastUpdate();
    }

    public void resetNavigationButtons() {
        buttonNorth.setEnabled(false);
        buttonEast.setEnabled(false);
        buttonSouth.setEnabled(false);
        buttonWest.setEnabled(false);
        buttonAreaOptions.setEnabled(false);
        buttonOverview.setEnabled(false);
    }

    private void updateNavigationButtons(Point point) {
        // reset buttons to all disabled
        resetNavigationButtons();
        if (player.getGameStatus() != GameStatus.PLAYING) {
            // disable navigation when the game is lost/won
            return;
        }

        buttonAreaOptions.setEnabled(true);
        buttonOverview.setEnabled(true);

        EnumSet<Direction> directions = gameData.getPossibleMoves(point);
        if (directions.contains(Direction.NORTH)) {
            buttonNorth.setEnabled(true);
        }
        if (directions.contains(Direction.EAST)) {
            buttonEast.setEnabled(true);
        }
        if (directions.contains(Direction.SOUTH)) {
            buttonSouth.setEnabled(true);
        }
        if (directions.contains(Direction.WEST)) {
            buttonWest.setEnabled(true);
        }
    }

    public void updateUI() {
        Area area = gameData.getPlayerArea();
        // check movable directions and enable buttons accordingly
        updateNavigationButtons(player.getLocation());
        areaBackground.setImageResource(area.isTown() ? R.drawable.ic_building1 : R.drawable.ic_tree3);
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, NavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onPause() {
        gameData.unregisterBroadcastReceiver(this, mReceiver);
        super.onPause();
    }
}
