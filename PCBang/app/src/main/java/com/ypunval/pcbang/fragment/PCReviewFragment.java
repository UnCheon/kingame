package com.ypunval.pcbang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.ReviewActivity;
import com.ypunval.pcbang.adapter.ReviewRVA;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Review;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class PCReviewFragment extends BaseRealmFragment {
    private static final String TAG = PCReviewFragment.class.getName();

    @Bind(R.id.rv_review)
    RecyclerView rv_review;

    int pcBangId;
    ReviewRVA adapter;

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
        this.pcBangId = pcBangId;
        setData();
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            pcBangId = getArguments().getInt("pcBangId");
        }

        setData();
    }

    private void setData() {
        realm = Realm.getDefaultInstance();
        PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();
        ArrayList<Review> reviews = new ArrayList<>();
        RealmResults<Review> results = realm.where(Review.class).equalTo("pcBang.id", pcBangId).findAllSorted("id", Sort.DESCENDING);
        for (int i = 0 ; i < results.size() ; i++) {
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
                getContext().startActivity(intent);
            }
        };

        adapter = new ReviewRVA(reviews, listener, pcBang);
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pc_review, container, false);
        ButterKnife.bind(this, view);

        rv_review.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_review.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
