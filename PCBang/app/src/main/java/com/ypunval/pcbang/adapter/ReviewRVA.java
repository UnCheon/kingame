package com.ypunval.pcbang.adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Review;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewRVA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private final ArrayList<Review> mValues;
    private final PCBangListenerInterface.OnReviewClickListener listener;
    PCBang pcBang;

    public ReviewRVA(ArrayList<Review> items, PCBangListenerInterface.OnReviewClickListener listener, PCBang pcBang) {
        mValues = items;
        this.listener = listener;
        this.pcBang = pcBang;
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 10;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_pc_review, parent, false);
            }else{
                view =LayoutInflater.from(parent.getContext()).inflate(R.layout.header_pc_review_pre_5, parent, false);
            }
            return new HeaderViewHolder(view);
        } else {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pc_review, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pc_review_pre_5, parent, false);
            }

            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder header_holder = (HeaderViewHolder) holder;
            header_holder.tv_average_rate.setText(pcBang.getAverageRate()+"");
            header_holder.tv_review_count.setText(pcBang.getReviewCount()+"");


            header_holder.btn_write_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReviewWriteClick();
                }
            });

        } else if (holder instanceof ItemViewHolder) {

            final ItemViewHolder item_holder = (ItemViewHolder) holder;
//            item_holder.mItem = mValues.get(position-1);
//            item_holder.tv_rate.setText(String.valueOf(item_holder.mItem.getRate()));
//            item_holder.tv_nickname.setText(item_holder.mItem.getNickname());
//            item_holder.tv_created.setText(item_holder.mItem.getCreated());
//            item_holder.tv_content.setText(item_holder.mItem.getContent());

//            item_holder.mItem = mValues.get(position-1);
            item_holder.tv_rate.setText("0");
            item_holder.tv_nickname.setText("zz");
            item_holder.tv_created.setText("20123");
            item_holder.tv_content.setText("asdfafdadsf");


            item_holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        listener.onReviewClick(item_holder.mItem);
                    }
                }
            });
        }
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_rate)
        TextView tv_rate;
        @Bind(R.id.tv_nickname)
        TextView tv_nickname;
        @Bind(R.id.tv_created)
        TextView tv_created;
        @Bind(R.id.tv_content)
        TextView  tv_content;
        public Review mItem;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.tv_average_rate)
        TextView tv_average_rate;
        @Bind(R.id.tv_review_count)
        TextView tv_review_count;
        @Bind(R.id.btn_write_review)
        Button btn_write_review;
        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
