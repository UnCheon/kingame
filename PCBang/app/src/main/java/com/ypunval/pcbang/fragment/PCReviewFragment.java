package com.ypunval.pcbang.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.MainMapActivity;
import com.ypunval.pcbang.activity.ReviewActivity;
import com.ypunval.pcbang.activity.SplashActivity;
import com.ypunval.pcbang.adapter.ReviewRVA;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Review;
import com.ypunval.pcbang.util.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PCReviewFragment extends BaseRealmFragment {
    private static final String TAG = PCReviewFragment.class.getName();
    public static final int WRITE_REVIEW_CODE = 22;


    @Bind(R.id.rv_review)
    RecyclerView rv_review;

    int pcBangId;

    int scrollY = 0;

    private PCBangListenerInterface.OnReviewClickListener listener;

    public PCReviewFragment() {
    }


    public static PCReviewFragment newInstance(int pcBangId) {
        PCReviewFragment fragment = new PCReviewFragment();
        Bundle args = new Bundle();
        args.putInt("pcBangId", pcBangId);
        fragment.setArguments(args);
        return fragment;
    }

    public void selectedPCBang(int pcBangId) {
        Log.i(TAG, "selectedPCBang: clicked");
        this.pcBangId = pcBangId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            pcBangId = getArguments().getInt("pcBangId");
        }
    }

    public void setData() {
        Realm realm = Realm.getDefaultInstance();
        try {
            PCBang pcBang = realm.where(PCBang.class).equalTo("id", Constant.pcBangId).findFirst();
            Log.i(TAG, "setData: start");
            ArrayList<Review> reviews = new ArrayList<>();
            RealmResults<Review> results = realm.where(Review.class).equalTo("pcBang.id", pcBangId).findAllSorted("id", Sort.DESCENDING);
            for (int i = 0; i < results.size(); i++) {
                reviews.add(results.get(i));
            }

            listener = new PCBangListenerInterface.OnReviewClickListener() {
                @Override
                public void onReviewClick(Review review) {

                }

                @Override
                public void onReviewWriteClick() {
                    Intent intent = new Intent(getContext(), ReviewActivity.class);
                    intent.putExtra("pcBangId", pcBangId);
                    startActivityForResult(intent, WRITE_REVIEW_CODE);
                }
            };

            ReviewRVA adapter = new ReviewRVA(reviews, listener, pcBang);
            rv_review.setAdapter(adapter);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.i(TAG, "setData: end");
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");

        View view = inflater.inflate(R.layout.fragment_pc_review, container, false);
        ButterKnife.bind(this, view);


        rv_review.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                Log.i(TAG, "onScrollStateChanged: " + scrollY);
                if (scrollY == 0){
                    ((MainMapActivity)getContext()).setCanBottomSheetScroll(true);
                }else{
                    ((MainMapActivity)getContext()).setCanBottomSheetScroll(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                scrollY += dy;
            }
        });

        setData();

        return view;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }
}
