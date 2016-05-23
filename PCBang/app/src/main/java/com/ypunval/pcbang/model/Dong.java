package com.ypunval.pcbang.model;



import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Dong extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private Doe doe;
    private Si si;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Doe getDoe() {
        return doe;
    }

    public void setDoe(Doe doe) {
        this.doe = doe;
    }

    public Si getSi() {
        return si;
    }

    public void setSi(Si si) {
        this.si = si;
    }
}
