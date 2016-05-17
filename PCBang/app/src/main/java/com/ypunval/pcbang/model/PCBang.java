package com.ypunval.pcbang.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class PCBang extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private int pcBangNumber;
    private int cafeNo;
    private String phoneNumber;
    private String computer;
    private RealmList<Convenience> convenience;
    private String address1;
    private String address2;
    private Doe doe;
    private Si si;
    private Dong dong;
    private float latitude;
    private float longitude;
    private RealmList<Subway> subway;
    private RealmList<Game> game;
    private int reviewCount;

    private float totalRate;
    private float averageRate;
    private int allianceLevel;

    private String allied;
    private String updated;
    private String images;
    private String imageMain;
    private String imageThumb;
    private String description1;
    private String description2;

    private String price;
    private int minPrice;
    private String seat;

    private int totalSeat;
    private int leftSeat;

    private boolean exist;

    @Ignore
    float distance;
    @Ignore
    boolean selected;


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

    public int getPcBangNumber() {
        return pcBangNumber;
    }

    public void setPcBangNumber(int pcBangNumber) {
        this.pcBangNumber = pcBangNumber;
    }

    public int getCafeNo() {
        return cafeNo;
    }

    public void setCafeNo(int cafeNo) {
        this.cafeNo = cafeNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getComputer() {
        return computer;
    }

    public void setComputer(String computer) {
        this.computer = computer;
    }

    public RealmList<Convenience> getConvenience() {
        return convenience;
    }

    public void setConvenience(RealmList<Convenience> convenience) {
        this.convenience = convenience;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public Dong getDong() {
        return dong;
    }

    public void setDong(Dong dong) {
        this.dong = dong;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public RealmList<Subway> getSubway() {
        return subway;
    }

    public void setSubway(RealmList<Subway> subway) {
        this.subway = subway;
    }

    public RealmList<Game> getGame() {
        return game;
    }

    public void setGame(RealmList<Game> game) {
        this.game = game;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public float getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(float totalRate) {
        this.totalRate = totalRate;
    }

    public float getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(float averageRate) {
        this.averageRate = averageRate;
    }

    public int getAllianceLevel() {
        return allianceLevel;
    }

    public void setAllianceLevel(int allianceLevel) {
        this.allianceLevel = allianceLevel;
    }

    public String getAllied() {
        return allied;
    }

    public void setAllied(String allied) {
        this.allied = allied;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImageMain() {
        return imageMain;
    }

    public void setImageMain(String imageMain) {
        this.imageMain = imageMain;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public int getTotalSeat() {
        return totalSeat;
    }

    public void setTotalSeat(int totalSeat) {
        this.totalSeat = totalSeat;
    }

    public int getLeftSeat() {
        return leftSeat;
    }

    public void setLeftSeat(int leftSeat) {
        this.leftSeat = leftSeat;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
