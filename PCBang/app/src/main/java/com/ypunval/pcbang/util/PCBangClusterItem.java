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

    public PCBangClusterItem(PCBang pcBang){
        id = pcBang.getId();
        pcBangName = pcBang.getName();
        minPrice = pcBang.getMinPrice();
        allianceLevel = pcBang.getAllianceLevel();
        latLng = new LatLng(pcBang.getLatitude(), pcBang.getLongitude());
        pcBang.setSelected(false);
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public int getId() {
        return id;
    }

    public String getPcBangName() {
        return pcBangName;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getAllianceLevel() {
        return allianceLevel;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
