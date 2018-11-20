package co.sgfc.madgame;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import co.sgfc.madgame.Items.ItemOwner;

public class Area extends DatabaseItem implements ItemManager {
    private boolean town;
    private List<Item> items;
    private String description;
    private boolean starred;
    private boolean explored;
    private Point position;

    public Area(boolean town) {
        super();
        setTown(town);
        setItems(new ArrayList<Item>());
        setDescription("");
        setStarred(false);
        setExplored(false);
        setPosition(new Point(0, 0));
    }

    public boolean isTown() {
        return town;
    }

    public void setTown(boolean town) {
        this.town = town;
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
        item.setItemOwner(ItemOwner.MAP);
        item.setPosition(position);
        this.items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
