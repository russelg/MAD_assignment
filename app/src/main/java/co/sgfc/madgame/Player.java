package co.sgfc.madgame;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.sgfc.madgame.Items.General.IceScraper;
import co.sgfc.madgame.Items.General.JadeMonkey;
import co.sgfc.madgame.Items.General.Roadmap;
import co.sgfc.madgame.Items.ItemOwner;

import static java.lang.Double.max;
import static java.lang.Math.min;

public class Player extends DatabaseItem implements ItemManager {
    private Point location;
    private double cash;
    private double health;
    private List<Item> items;

    public Player() {
        super();
        reset();
    }

    public void reset() {
        this.location = new Point(0, 0);
        this.setCash(0);
        this.setHealth(100.0);
        this.setItems(new ArrayList<Item>());
    }

    public Point getLocation() {
        return new Point(this.location);
    }

    public void setLocation(Point point) {
        this.location = new Point(point);
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public void incrementCash(double increment) {
        this.cash += increment;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = Math.min(health, 100.0);
    }

    public void incrementHealth(double increment) {
        this.health = Math.min(this.health + increment, 100.0);
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public void addItem(Item item) {
        item.setItemOwner(ItemOwner.PLAYER);
        this.items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public double getEquipmentMass() {
        double mass = 0.0;
        for (Item e : this.items) {
            if (e instanceof Equipment)
                mass += ((Equipment) e).getMass();
        }

        return mass;
    }

    public GameStatus getGameStatus() {
        if (getHealth() <= 0.0) {
            return GameStatus.LOST;
        }

        List<Class<? extends Equipment>> winningItems = new ArrayList<>(GameData.winningItems);
        for (Item item : items) {
            Class itemCls = item.getClass();
            winningItems.remove(itemCls);
        }
        if (winningItems.size() == 0) {
            return GameStatus.WON;
        }

        return GameStatus.PLAYING;
    }
}
