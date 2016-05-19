package com.ypunval.pcbang.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.ypunval.pcbang.R;

import java.util.Random;

/**
 * Created by uncheon on 16. 5. 9..
 */
public class PCBangRenderer extends DefaultClusterRenderer<PCBangClusterItem> {
    private IconGenerator mIconGenerator;
    private final LinearLayout linearLayout;
    private final int mDimension;
    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
    Context context;

    public PCBangRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
        this.context = context;

        mIconGenerator = new IconGenerator(context);

        mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
        int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);



        linearLayout = (LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.marker, null);


        mIconGenerator.setContentView(linearLayout);
        mIconGenerator.setBackground(TRANSPARENT_DRAWABLE);
    }
    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public IconGenerator getmIconGenerator() {
        return mIconGenerator;
    }

    @Override
    protected void onBeforeClusterItemRendered(PCBangClusterItem pcBangClusterItem, MarkerOptions markerOptions) {

        int price = pcBangClusterItem.getMinPrice();
        String str = String.format("%,d", 1000);

        TextView tv_name = (TextView)linearLayout.findViewById(R.id.tv_name);
        TextView tv_price = (TextView)linearLayout.findViewById(R.id.tv_price);

        tv_name.setText(pcBangClusterItem.getPcBangName());
        tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorDarkAccent));

        int level = pcBangClusterItem.getAllianceLevel();


        tv_price.setText("1,200");

        // TODO: 2016. 5. 19. _level을 pcBangClusterItem에서 가져온 alliance_level을 이용한다.
        Random random = new Random();
        int _level = random.nextInt(3);

        Util.setMarkerLinearLayout(context, linearLayout, _level);

        switch (_level){
            case 0:
                linearLayout.setBackgroundResource(R.drawable.bubble_grey_small);
                tv_price.setVisibility(View.GONE);
                tv_price.setTextColor(ContextCompat.getColor(context, R.color.colorDarkAccent));
                tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorDarkAccent));
                break;
            case 1:
                linearLayout.setBackgroundResource(R.drawable.bubble_grey);
                tv_price.setVisibility(View.VISIBLE);
                tv_price.setTextColor(ContextCompat.getColor(context, R.color.colorDarkAccent));
                tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorDarkAccent));

                break;
            case 2:
                linearLayout.setBackgroundResource(R.drawable.bubble_primary);
                tv_price.setVisibility(View.VISIBLE);
                tv_price.setTextColor(ContextCompat.getColor(context, R.color.white));
                tv_name.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
        }


        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));


    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 9;
    }
}