package com.ypunval.pcbang.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.ypunval.pcbang.R;

/**
 * Created by uncheon on 16. 4. 22..
 */
public class SliderPCBangInfoView extends BaseSliderView {

    public SliderPCBangInfoView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.slider_info_render, null);
        ImageView target = (ImageView) v.findViewById(R.id.slider_image);
        bindEventAndShow(v, target);
        return v;
    }
}
