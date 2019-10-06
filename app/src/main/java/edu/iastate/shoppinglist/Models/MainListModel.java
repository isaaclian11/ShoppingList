package edu.iastate.shoppinglist.Models;

import java.io.Serializable;
import java.util.UUID;

public class MainListModel implements Serializable {
    private UUID id;
    private String title;

    public MainListModel(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}
