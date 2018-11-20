package co.sgfc.madgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import co.sgfc.madgame.Database.Model;
import co.sgfc.madgame.Items.General.*;
import co.sgfc.madgame.Items.General.Food.*;
import co.sgfc.madgame.Items.Usables.*;

public class GameData {
    private static GameData sGameData;

    // common, findable items
    public static List<Class<? extends Item>> gameItems = Arrays.asList(
            Apple.class, Banana.class, Chips.class, Grass.class, Pie.class, Sandwich.class,
            PoisonIvy.class, BenKenobi.class, ImprobabilityDrive.class, SmellOScope.class);

    // items which win the game
    public static List<Class<? extends Equipment>> winningItems = Arrays.asList(
            JadeMonkey.class, Roadmap.class, IceScraper.class);

    public static String ACTION_DATA_UPDATED = "DATA_UPDATED";

    private int rows, cols;
    private Area[][] grid;
    private Player player;
    private Model model;
    private Context context;

    public Context getContext() {
        return context;
    }

    public static GameData getInstance() {
        if (sGameData == null) {
            throw new IllegalArgumentException("no context provided previously");
        }

        return sGameData;
    }

    public static GameData getInstance(Context context) {
        if (sGameData == null) {
            sGameData = new GameData(6, 6, context);
        }
        return sGameData;
    }

    private GameData(int row, int col, Context context) {
        rows = row - 1;
        cols = col - 1;
        grid = new Area[row][col];
        this.context = context;
        this.model = new Model(this.context);
    }

    public void broadcastUpdate() {
        if (context != null)
            context.sendBroadcast(new Intent(ACTION_DATA_UPDATED));
    }

    public Player getPlayer() {
        if (player == null) {
            player = model.getPlayer();
            if (player == null) {
                player = new Player();
                initializePlayer();
                savePlayer();
            }

            Area playerLoc = getArea(player.getLocation());
            playerLoc.setExplored(true);
            updateArea(playerLoc);
        }
        return player;
    }

    public void getGameMap() {
        Area[][] map = model.getMap(rows, cols);
        if (map != null) {
            setGrid(map);
        } else {
            initializeGameMap();
            saveGameMap();
        }
    }

    public void saveGameMap() {
        // clear current map from database
        model.removeMap();

        for (int i = 0; i < (rows + 1); i++) {
            for (int j = 0; j < (cols + 1); j++) {
                Area area = getArea(new Point(i, j));
                model.addMapCell(area);
            }
        }
    }

    public void updateArea(Area area) {
        model.updateMapCell(area);
    }

    public void initializeGameMap() {
        Item item;
        Random random = new Random();
        grid = new Area[rows + 1][cols + 1];

        Collections.shuffle(gameItems);
        // create map of mapSize rows*cols
        for (int i = 0; i < rows + 1; i++) {
            for (int j = 0; j < cols + 1; j++) {
                boolean town = (random.nextInt(2) == 0);
                Point pos = new Point(i, j);
                Area area = new Area(town);

                int size = gameItems.size();
                int items = random.nextInt(2);
                // add a random number of random items to each area
                try {
                    area.setPosition(pos);
                    for (int k = 0; k <= items; k++) {
                        // create an instance of the class from the items class list
                        Class c = gameItems.get(random.nextInt(size));
                        item = (Item) c.newInstance();
                        item.setPosition(pos);
                        area.addItem(item);
                    }
                    setArea(pos, area);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        Collections.shuffle(winningItems);
        // add one of every winning item around the map randomly
        for (Class itemCls : winningItems) {
            Point randomPoint = new Point(random.nextInt(rows), random.nextInt(cols));
            Area area = getArea(randomPoint);
            try {
                // create an instance of the class from the items class list
                item = (Item) itemCls.newInstance();
                area.addItem(item);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializePlayer() {
        Random random = new Random();
        Point pos = new Point(random.nextInt(rows), random.nextInt(cols));
        Area area = getArea(pos);

        player.reset();
        player.setLocation(pos);
        area.setPosition(pos);
        area.setExplored(true);
        model.updateMapCell(area);
    }

    public void updatePlayer() {
        model.updatePlayer(player);
    }

    public void savePlayer() {
        model.removePlayer();
        model.addPlayer(player);
    }

    public void saveData() {
        saveGameMap();
        savePlayer();
        broadcastUpdate();
    }

    public Point mapSize() {
        return new Point(rows + 1, cols + 1);
    }

    public EnumSet<Direction> getPossibleMoves(Point point) {
        EnumSet<Direction> directions = EnumSet.allOf(Direction.class);

        if (point.y <= 0) {
            directions.remove(Direction.NORTH);
        }

        if (point.y >= rows) {
            directions.remove(Direction.SOUTH);
        }

        if (point.x <= 0) {
            directions.remove(Direction.WEST);
        }

        if (point.x >= cols) {
            directions.remove(Direction.EAST);
        }

        return directions;
    }

    public void setArea(Point point, Area area) {
        if (point.x <= rows && point.y <= cols) {
            this.grid[point.x][point.y] = area;
        } else {
            throw new IllegalArgumentException("Given coordinates are out of bounds");
        }
    }

    public Area getPlayerArea() {
        return getArea(player.getLocation());
    }

    public List<Area> getPlayerSurroundingAreas(int inRows, int inCols) {
        List<Area> areas = new ArrayList<>();
        Area area;

        Point pos = player.getLocation();
        Point topLeft = new Point(pos);
        Point bottomRight = new Point(pos);
        topLeft.offset(-inRows, -inCols);
        bottomRight.offset(inRows, inCols);

        for (int i = topLeft.x; i < bottomRight.x; i++) {
            for (int j = topLeft.y; j < bottomRight.y; j++) {
                Point currPos = new Point(i, j);
                if ((currPos.x <= rows && currPos.y <= cols) &&
                        (currPos.x >= 0 && currPos.y >= 0)) {
                    area = getArea(currPos);
                    areas.add(area);
                }
            }
        }

        return areas;
    }

    public Area getArea(Point point) {
        if (point.x <= rows && point.y <= cols)
            return this.grid[point.x][point.y];

        throw new IllegalArgumentException("Given coordinates are out of bounds");
    }

    public void setGrid(Area[][] inGrid) {
        grid = inGrid;
    }

    public BroadcastReceiver registerBroadcastReceiver(Context context, Runnable callback) {
        final Runnable func = callback;
        IntentFilter filter = new IntentFilter(GameData.ACTION_DATA_UPDATED);
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (GameData.ACTION_DATA_UPDATED.equals(intent.getAction())) {
                    func.run();
                }
            }
        };
        context.registerReceiver(mReceiver, filter);
        return mReceiver;
    }

    public void unregisterBroadcastReceiver(Context context, BroadcastReceiver mReceiver) {
        try {
            context.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            // no better way to handle unregistered receiver
        }
    }
}
