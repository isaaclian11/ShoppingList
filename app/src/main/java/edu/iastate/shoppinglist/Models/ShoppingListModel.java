package edu.iastate.shoppinglist.Models;

import java.io.Serializable;
import java.util.UUID;

public class ShoppingListModel implements Serializable {

    private UUID parentID;
    private String item;

    public ShoppingListModel(UUID parentID, String item) {
        this.parentID = parentID;
        this.item = item;
    }

    public UUID getParentID() {
        return parentID;
    }

    public String getItem() {
        return item;
    }
}
