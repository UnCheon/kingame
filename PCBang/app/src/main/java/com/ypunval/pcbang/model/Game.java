package com.ypunval.pcbang.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Game extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String created;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
