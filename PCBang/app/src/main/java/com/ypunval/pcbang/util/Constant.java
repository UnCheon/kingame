package com.ypunval.pcbang.util;

import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by uncheon on 16. 4. 15..
 */
public class Constant {
    public static final float DEFAULT_KILOMETER = 3.0f;
    public static final float LATITUDE_CONSTANT = 0.0090107f;
    public static final float LONGITUDE_CONSTANT = 0.0112476f;
    public static final String GOOGLE_MAP_KEY = "AIzaSyBKpfsEPC8sYp_aTpetzYr4PTQLSV4mIjI";
    public static ArrayList<PCBang> near_pcBangs = new ArrayList<>();
    public static ArrayList<Convenience> conveniences = new ArrayList<>();
    public static ArrayList<Convenience> selectedConveniences = new ArrayList<>();

    public static int rangeKm = 3;

    public static int mapRangeKm = 3;

    public static int subwayRangeKm = 1;

    public static PCBang clicked_pcBang;


    public static void addSelectedConvenience(Convenience convenience) {
        selectedConveniences.add(convenience);
        conveniences.remove(convenience);
    }

    public static void removeSelectedConvenience(Convenience convenience) {
        selectedConveniences.remove(convenience);
        conveniences.add(convenience);
        Collections.sort(conveniences, new Comparator<Convenience>() {
            @Override
            public int compare(Convenience obj1, Convenience obj2) {
                return (obj1.getOrder() < obj2.getOrder()) ? -1 : (obj1.getOrder() > obj2.getOrder()) ? 1 : 0;
            }
        });
    }
}
