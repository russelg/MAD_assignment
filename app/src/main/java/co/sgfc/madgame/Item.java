package co.sgfc.madgame;

import android.content.Context;
import android.graphics.Point;

import co.sgfc.madgame.Items.ItemOwner;

abstract public class Item extends DatabaseItem {
    private double value;
    private String description;
    private Point position;
    private ItemOwner owner = ItemOwner.NONE;

    public Item(String description, double value) {
        super();
        setDescription(description);
        setValue(value);
    }

    public double getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    abstract public boolean isUsable();

    public void use() {
    }

    public void use(Context context) {
        use();
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    abstract public String getID();

    public ItemOwner getItemOwner() {
        return owner;
    }

    public void setItemOwner(ItemOwner owner) {
        this.owner = owner;
    }
}
