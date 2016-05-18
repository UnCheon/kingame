package com.ypunval.pcbang.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainMapActivity;
import com.ypunval.pcbang.model.PCBang;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PCGalleryFragment extends BaseRealmFragment {

    @Bind(R.id.nsv)
    NestedScrollView nsv;

    @Bind(R.id.tv_average_rate)
    TextView tv_average_rate;
    @Bind(R.id.tv_review_count)
    TextView tv_review_count;
    @Bind(R.id.tv_current_seat)
    TextView tv_current_seat;
    @Bind(R.id.tv_total_seat)
    TextView tv_total_seat;
    @Bind(R.id.tv_conveniences)
    TextView tv_conveniences;
    @Bind(R.id.tv_address)
    TextView tv_address;
    @Bind(R.id.tv_subways)
    TextView tv_subways;
    @Bind(R.id.tv_phone_number)
    TextView tv_phone_number;

    private static final String TAG = PCGalleryFragment.class.getName();

    int pcBangId;
    PCBang pcBang;


    public PCGalleryFragment() {

    }

    public static PCGalleryFragment newInstance(int pcBangId) {
        PCGalleryFragment fragment = new PCGalleryFragment();
        Bundle args = new Bundle();
        args.putInt("pcBangId", pcBangId);
        fragment.setArguments(args);
        return fragment;
    }


    public void selectedPCBang(int pcBangId){
        Log.i(TAG, "selectedPCBang: clicked");
        this.pcBangId = pcBangId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pcBangId = getArguments().getInt("pcBangId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            view = inflater.inflate(R.layout.fragment_pc_basic, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_pc_basic_pre_5, container, false);
        }
        ButterKnife.bind(this, view);

        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.i(TAG, "onScrollChange: " + scrollY + "  " + oldScrollY);
                if (scrollY == 0 && scrollY - oldScrollY < 0){
                    ((MainMapActivity)getContext()).setCanBottomSheetScroll(true);
                }else{
                    ((MainMapActivity)getContext()).setCanBottomSheetScroll(false);
                }
            }
        });

        return view;
    }


    private void setData() {
        tv_average_rate.setText(pcBang.getAverageRate() + "");
        tv_review_count.setText(pcBang.getReviewCount() + "");
        tv_current_seat.setText(pcBang.getLeftSeat() + "");
        tv_total_seat.setText(pcBang.getTotalSeat() + "");
        tv_address.setText(pcBang.getAddress1());
        tv_phone_number.setText(pcBang.getPhoneNumber());
    }


    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
        pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();
        setData();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
