package com.ypunval.pcbang.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.ypunval.pcbang.model.PCBang;

/**
 * Created by uncheon on 16. 4. 12..
 */
public class PCBangClusterItem implements ClusterItem {

    int id;
    String pcBangName;
    int minPrice;
    int allianceLevel;


    LatLng latLng;

    public PCBangClusterItem(){

    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPcBangName() {
        return pcBangName;
    }

    public void setPcBangName(String pcBangName) {
        this.pcBangName = pcBangName;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getAllianceLevel() {
        return allianceLevel;
    }

    public void setAllianceLevel(int allianceLevel) {
        this.allianceLevel = allianceLevel;
    }


}
