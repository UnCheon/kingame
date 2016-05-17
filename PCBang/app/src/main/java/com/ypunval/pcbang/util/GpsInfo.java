package com.ypunval.pcbang.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


/**
 * Created by uncheon on 16. 4. 13..
 */
public class GpsInfo extends Service implements LocationListener {
    public void onLocationFinish(){

    }



    private final Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public GpsInfo(Context context) {
        this.mContext = context;
    }


    public void getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            // GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);



            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
                onLocationFinish();
                return;
            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lat != 0 && lon != 0) {
            String address = getAddress(lat, lon);
            SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = mPref.edit();
            editor.putFloat("latitude", (float) lat);
            editor.putFloat("longitude", (float) lon);
            editor.putString("address", address);
            editor.commit();

            stopUsingGPS();


            Toast.makeText(
                    mContext,
                    "당신의 위치 - \n위도: " + lat + "\n경도: " + lon,
                    Toast.LENGTH_LONG).show();

        } else {
            stopUsingGPS();
            showFailedAlert();
        }

        onLocationFinish();

    }


    public String getAddress(double lat, double lng) {
        String address = "";
        try {
            //위치정보를 활용하기 위한 구글 API 객체
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());


            //주소 목록을 담기 위한 HashMap
            List<Address> list = null;

            try {
                list = geocoder.getFromLocation(lat, lng, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (list == null) {
                Log.e("getAddress", "주소 데이터 얻기 실패");
                return null;
            }

            String address_ = list.get(0).getAddressLine(0);
            String city = list.get(0).getAddressLine(1);
            String country = list.get(0).getAddressLine(2);
            System.out.println(address_ + " - " + city + " - " + country);


            if (list.size() > 0) {
                Address addr = list.get(0);
                address = addr.getAddressLine(0);

                System.out.println(addr);
            }

        } catch (Exception e) {


        }
        return address;
    }

    /**
     * GPS 종료
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsInfo.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("알림");
        alertDialog.setMessage("위치정보를 확인할 수 없습니다.\n 위치서비스를 켜주세요.");





        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void showFailedAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("알림");
        alertDialog.setMessage("위치정보를 가져올수 없습니다.");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}