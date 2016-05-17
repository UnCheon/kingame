package com.ypunval.pcbang.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by uncheon on 16. 4. 9..
 */
public class Review extends RealmObject {
    @PrimaryKey
    private int id;

    private float rate;
    private String nickname;
    private String created;
    private String content;
    private String phoneNumber;

    private PCBang pcBang;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PCBang getPcBang() {
        return pcBang;
    }

    public void setPcBang(PCBang pcBang) {
        this.pcBang = pcBang;
    }
}
