package com.ypunval.pcbang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Si;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SiRVA extends RecyclerView.Adapter<SiRVA.ViewHolder> {

    private final List<Si> mValues;
    private final PCBangListenerInterface.OnSiClickListener listener;


    public SiRVA(List<Si> items, PCBangListenerInterface.OnSiClickListener listener) {
        mValues = items;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_si, parent, false);
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
                    listener.onSiClick(holder.mItem);
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

        public Si mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
