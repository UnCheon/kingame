package com.ypunval.pcbang.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.model.PCBang;

/**
 * Created by uncheon on 16. 4. 22..
 */
public class SliderPCBangAdvertiseView extends BaseSliderView {
    PCBang pcBang;
    public SliderPCBangAdvertiseView(Context context, PCBang pcBang) {
        super(context);
        this.pcBang = pcBang;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.slider_main_render,null);
        ImageView target = (ImageView)v.findViewById(R.id.slider_image);
        TextView tvPCName = (TextView)v.findViewById(R.id.tvPCName);
        TextView tvDescription1 = (TextView)v.findViewById(R.id.tvDescription1);
        TextView tvDescription2 = (TextView)v.findViewById(R.id.tvDescription2);
        TextView tvDistance = (TextView)v.findViewById(R.id.tvDistance);


        if (pcBang != null){
            tvPCName.setText(pcBang.getName());
            tvDescription1.setText(pcBang.getDescription1());
            tvDescription2.setText(pcBang.getMinPrice()+"");
            float dist = pcBang.getDistance();

            String distance = "";
            if (dist <= 1000) {
                int i_meter = (int) dist;
                distance = i_meter + "m";
            } else {
                float kilometer = (float) (dist / 1000.0);
                distance = String.format("%.1f", kilometer) + "km";
            }
            tvDistance.setText(distance);
            if (pcBang.getMinPrice() == 1000000)
                tvDescription2.setText("?");
            else
                tvDescription2.setText(pcBang.getMinPrice() + "");

            bindEventAndShow(v, target);
        }

        return v;
    }

    public PCBang getPcBang() {
        return pcBang;
    }

    public void setPcBang(PCBang pcBang) {
        this.pcBang = pcBang;
    }
}
