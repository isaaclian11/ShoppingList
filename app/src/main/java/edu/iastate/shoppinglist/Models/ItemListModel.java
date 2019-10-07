package edu.iastate.shoppinglist.Models;

import java.io.Serializable;
import java.util.UUID;

public class ItemListModel implements Serializable {

    private UUID parentID;
    private String item;

    /**
     * Constructor for the ItemListModel
     * @param parentID Id of the parent of an item
     * @param item An item in a shopping list
     */
    public ItemListModel(UUID parentID, String item) {
        this.parentID = parentID;
        this.item = item;
    }

    /**
     * Returns the parent id of an item
     * @return Returns the parent id of an item
     */
    public UUID getParentID() {
        return parentID;
    }

    /**
     * Returns the name an item
     * @return Returns the name of an item
     */
    public String getItem() {
        return item;
    }
}
