package com.ypunval.pcbang.update;

import android.content.Context;
import android.util.Log;

import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.Doe;
import com.ypunval.pcbang.model.Dong;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Review;
import com.ypunval.pcbang.model.Si;
import com.ypunval.pcbang.model.Subway;
import com.ypunval.pcbang.model.Sync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by uncheon on 16. 5. 3..
 */
public class JSONToRealm {
    private static final String TAG = "JSONToRealm";
    Context context;

    public JSONToRealm(Context context) {
        this.context = context;
    }

    public String registerResultToRealm(String responseString) {
        String status = "fail";
        responseString = responseString.replace("\\\"", "\"");
        responseString = responseString.replace("\\\\", "\\");
        int length = responseString.length();
        responseString = responseString.substring(1, length - 1);

        Log.i(TAG, "register response : " + responseString);

        try {
            JSONObject responseObj = new JSONObject(responseString);
            status = responseObj.getString("status");

        } catch (JSONException e) {
            status = "fail";
            e.printStackTrace();
        }
        return status;
    }

    public String nicknameResultToRealm(String responseString) {
        String status = "fail";
        responseString = responseString.replace("\\\"", "\"");
        responseString = responseString.replace("\\\\", "\\");
        int length = responseString.length();
        responseString = responseString.substring(1, length - 1);

        Log.i(TAG, "nickname response : " + responseString);

        try {
            JSONObject responseObj = new JSONObject(responseString);
            status = responseObj.getString("status");

        } catch (JSONException e) {
            status = "fail";
            e.printStackTrace();
        }
        return status;
    }

    public String updateResultToRealm(String responseString) {
        Realm realm = Realm.getDefaultInstance();

        String status = "fail";
        responseString = responseString.replace("\\\"", "\"");
        responseString = responseString.replace("\\\\", "\\");
        int length = responseString.length();
        responseString = responseString.substring(1, length - 1);

        Log.i(TAG, "update response : " + responseString);


        try {
            JSONObject responseObj = new JSONObject(responseString);
            status = responseObj.getString("status");
            if (status.equals("success")) {
                realm.beginTransaction();

                JSONObject result = responseObj.getJSONObject("result");
                JSONObject sync_obj = responseObj.getJSONObject("sync");

                JSONArray pcBang_list = result.getJSONArray("pcbang_list");
                JSONArray review_list = result.getJSONArray("review_list");


                ArrayList<PCBang> pcBangs = new ArrayList<>();
                for (int i = 0; i < pcBang_list.length(); i++) {
                    JSONObject obj = (JSONObject) pcBang_list.get(i);
                    PCBang pcBang = new PCBang();
                    pcBang.setId(obj.getInt("id"));
                    pcBang.setTotalSeat(obj.getInt("total_seat"));

                    pcBang.setSeat(obj.getString("seat"));
                    pcBang.setCafeNo(obj.getInt("cafe_no"));
                    pcBang.setComputer(obj.getString("computer"));
                    boolean exist;
                    if (obj.getInt("exist") == 0)
                        exist = false;
                    else
                        exist = true;

                    pcBang.setExist(exist);
                    pcBang.setImages("images");
                    pcBang.setImageMain("imageMain");
                    pcBang.setImageThumb("imageThumb");
                    pcBang.setReviewCount(obj.getInt("review_count"));
                    pcBang.setMinPrice(obj.getInt("min_price"));
                    pcBang.setLatitude((float) obj.getDouble("latitude"));
                    pcBang.setPhoneNumber(obj.getString("phone_number"));
                    pcBang.setUpdated(obj.getString("updated"));
                    pcBang.setAddress1(obj.getString("address1"));
                    pcBang.setPrice(obj.getString("price"));
                    pcBang.setTotalRate(obj.getInt("total_rate"));
                    pcBang.setDescription2(obj.getString("description2"));
                    pcBang.setDescription1(obj.getString("description1"));
                    pcBang.setLeftSeat(obj.getInt("left_seat"));
                    pcBang.setPcBangNumber(obj.getInt("pcbang_number"));
                    pcBang.setAverageRate((float) obj.getDouble("average_rate"));
                    pcBang.setName(obj.getString("name"));
                    pcBang.setAllianceLevel(obj.getInt("alliance_level"));
                    pcBang.setLongitude((float) obj.getDouble("longitude"));
                    pcBang.setAllied(obj.getString("allied"));

                    int doe_id = obj.getInt("doe_id");
                    int si_id = obj.getInt("si_id");
                    int dong_id = obj.getInt("dong_id");

                    Doe doe = realm.where(Doe.class).equalTo("id", doe_id).findFirst();
                    Si si = realm.where(Si.class).equalTo("id", si_id).findFirst();
                    Dong dong = realm.where(Dong.class).equalTo("id", dong_id).findFirst();


                    if (exist)
                        si.setPcBangCount(si.getPcBangCount() + 1);
                    else
                        si.setPcBangCount(si.getPcBangCount() - 1);

                    JSONArray ja_subways = obj.getJSONArray("subway");
                    RealmList<Subway> subways = new RealmList<Subway>();
                    for (int j = 0 ; j < ja_subways.length() ; j++){
                        Subway subway = realm.where(Subway.class).equalTo("id", (int)ja_subways.get(j)).findFirst();
                        if (exist)
                            subway.setPcBangCount(subway.getPcBangCount() + 1);
                        else
                            subway.setPcBangCount(subway.getPcBangCount() - 1);
                        subways.add(subway);
                    }

                    JSONArray ja_conveniences = obj.getJSONArray("convenience");
                    RealmList<Convenience> conveniences = new RealmList<Convenience>();
                    for (int j = 0 ; j < ja_conveniences.length() ; j++){
                        Convenience convenience = realm.where(Convenience.class).equalTo("id", (int)ja_conveniences.get(j)).findFirst();
                        conveniences.add(convenience);
                    }

                    pcBang.setDoe(doe);
                    pcBang.setSi(si);
                    pcBang.setDong(dong);
                    pcBangs.add(pcBang);

                }

                ArrayList<Review> reviews = new ArrayList<>();
                for (int i = 0; i < review_list.length(); i++) {
                    JSONObject obj = review_list.getJSONObject(i);

                    Review review = new Review();
                    review.setCreated(obj.getString("created"));
                    review.setContent(obj.getString("content"));
                    review.setRate((float) obj.getDouble("rate"));
                    review.setPhoneNumber(obj.getString("phoneNumber"));
                    int pcBang_id = obj.getInt("pcbang");
                    review.setNickname(obj.getString("nickname"));
                    review.setId(obj.getInt("id"));

                    PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBang_id).findFirst();
                    review.setPcBang(pcBang);

                    reviews.add(review);
                }


                Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
                sync.setPcBangCount(sync_obj.getInt("pcbang_count"));
                sync.setS3(sync_obj.getString("s3_url"));
                sync.setUpdated(sync_obj.getString("updated"));
                sync.setLastRequsetTime(System.currentTimeMillis());

                realm.copyToRealmOrUpdate(pcBangs);
                realm.copyToRealmOrUpdate(reviews);


                realm.commitTransaction();


            } else if (status.equals("empty")) {
                realm.beginTransaction();
                Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
                sync.setLastRequsetTime(System.currentTimeMillis());
                realm.commitTransaction();

            } else if (status.equals("fail")) {
//                fail
            }

        } catch (JSONException e) {
            status = "fail";
            e.printStackTrace();
        }

        realm.close();

        return status;
    }





}
