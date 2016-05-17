package com.ypunval.pcbang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Subway;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubwayRVA extends RecyclerView.Adapter<SubwayRVA.ViewHolder> {

    private final List<Subway> mValues;
    private final PCBangListenerInterface.OnSubwayClickListener listener;


    public SubwayRVA(List<Subway> items, PCBangListenerInterface.OnSubwayClickListener listener) {
        mValues = items;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.tv_name.setText(holder.mItem.getName());
        holder.tv_count.setText(holder.mItem.getPcBangCount()+"");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onSubwayClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @Bind(R.id.tv_name)
        TextView tv_name;
        @Bind(R.id.tv_count)
        TextView tv_count;


        public Subway mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);


        }
    }
}
