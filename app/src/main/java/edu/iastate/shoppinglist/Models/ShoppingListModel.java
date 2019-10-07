package edu.iastate.shoppinglist.Models;

import java.io.Serializable;
import java.util.UUID;

public class ShoppingListModel implements Serializable {
    private UUID id;
    private String title;

    /**
     * Constructor of this model
     * @param id id of the shopping list
     * @param title name of the shopping list
     */
    public ShoppingListModel(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Returns the id of the shopping list
     * @return Returns the id of the shopping list
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the name of the shopping list
     * @return Returns the name of the shopping list
     */
    public String getTitle() {
        return title;
    }

}
