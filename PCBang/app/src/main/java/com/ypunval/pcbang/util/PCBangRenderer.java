package com.ypunval.pcbang.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.ypunval.pcbang.R;

/**
 * Created by uncheon on 16. 5. 9..
 */
public class PCBangRenderer extends DefaultClusterRenderer<PCBangClusterItem> {
    private IconGenerator mIconGenerator;
    private final ImageView mImageView;
    private final TextView textView;
    private final int mDimension;

    public PCBangRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);

        mIconGenerator = new IconGenerator(context);

        mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
        int padding = (int) context.getResources().getDimension(R.dimen.custom_profile_padding);

        int marker_height = (int) context.getResources().getDimension(R.dimen.marker_height);
        int marker_padding = (int) context.getResources().getDimension(R.dimen.marker_padding);
        textView = (TextView) ((Activity)context).getLayoutInflater().inflate(R.layout.marker_unselected_price, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, marker_height));
        textView.setPadding(marker_padding, 0, marker_padding, 0);

        mImageView = new ImageView(context);
//        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            mImageView.setPadding(0, 0, 0, 0);
//        mIconGenerator.setContentView(mImageView);
        mIconGenerator.setContentPadding(0,0,0,0);
        mIconGenerator.setContentView(textView);


        mIconGenerator.setBackground(context.getResources().getDrawable(R.drawable.bg_transparent));
    }

    public ImageView getmImageView() {
        return mImageView;
    }
    public TextView getTextView() {return textView;}

    public IconGenerator getmIconGenerator() {
        return mIconGenerator;
    }

    @Override
    protected void onBeforeClusterItemRendered(PCBangClusterItem pcBangClusterItem, MarkerOptions markerOptions) {

        int price = pcBangClusterItem.getMinPrice();
        String str = String.format("%,d", 1000);
        textView.setText(str);

//            case 0:
//                mImageView.setImageResource(R.drawable.marker_red_unselected);
//                break;
//            case 1:
//                mImageView.setImageResource(R.drawable.marker_red_unselected);
//                break;
//            case 2:
//                mImageView.setImageResource(R.drawable.marker_red_unselected);
//                break;
//        }


        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(pcBangClusterItem.getPcBangName());

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 9;
    }
}