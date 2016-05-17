package com.ypunval.pcbang.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sync extends RealmObject {

    @PrimaryKey
    private int id;
    private String updated;
    private int period;
    private long lastRequsetTime;
    private int pcBangCount;
    private String s3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getLastRequsetTime() {
        return lastRequsetTime;
    }

    public void setLastRequsetTime(long lastRequsetTime) {
        this.lastRequsetTime = lastRequsetTime;
    }

    public int getPcBangCount() {
        return pcBangCount;
    }

    public void setPcBangCount(int pcBangCount) {
        this.pcBangCount = pcBangCount;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }
}