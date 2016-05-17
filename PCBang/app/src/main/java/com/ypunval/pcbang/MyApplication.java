package com.ypunval.pcbang;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.ypunval.pcbang.util.PCBangRealmModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by uncheon on 16. 4. 13..
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        RealmConfiguration defaultConfig = new RealmConfiguration.Builder(this).build();
        RealmConfiguration pcBangConfig = new RealmConfiguration.Builder(this)
                .name("pcbang.realm")
                .setModules(new PCBangRealmModule())
                .build();

        Realm.setDefaultConfiguration(pcBangConfig);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        fileCopy();

    }


    private void fileCopy() {
        File outfile = new File("/data/data/com.ypunval.pcbang/files/pcbang.realm");
        AssetManager assetManager = getResources().getAssets();


        InputStream is = null;
        try {
            is = assetManager.open("pcbang.realm", AssetManager.ACCESS_BUFFER);
            long fileSize = is.available();
            if(true){
//            if (outfile.length() < fileSize) {
                byte[] tempData = new byte[(int) fileSize];
                is.read(tempData);
                is.close();
                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempData);
                fo.close();
                Log.i("MyApplication", "copy ok");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
