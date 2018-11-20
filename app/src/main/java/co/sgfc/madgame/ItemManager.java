package co.sgfc.madgame;

import java.util.List;

interface ItemManager {
    List<Item> getItems();

    void setItems(List<Item> items);

    void addItem(Item item);

    void removeItem(Item item);
}
