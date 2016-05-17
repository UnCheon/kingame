package com.ypunval.pcbang.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Doe;

import java.util.List;

public class DoeRVA extends RecyclerView.Adapter<DoeRVA.ViewHolder> {

    private final List<Doe> mValues;
    private final PCBangListenerInterface.OnDoeClickListener listener;


    public DoeRVA(List<Doe> items, PCBangListenerInterface.OnDoeClickListener listener) {
        mValues = items;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.tv_name.setText(holder.mItem.getName());
        if (holder.mItem.isSelected()){
            holder.tv_name.setBackgroundResource(R.drawable.selector_grey);
            holder.tv_name.setTextColor(Color.parseColor("#333333"));
        }else{
            holder.tv_name.setBackgroundResource(R.drawable.selector);
            holder.tv_name.setTextColor(Color.parseColor("#6e6e6e"));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    for (int i = 0 ; i < mValues.size() ; i++){
                        Doe item = mValues.get(i);
                        if (i == position){
                            item.setSelected(true);
                        }else{
                            item.setSelected(false);
                        }
                    }
                    notifyDataSetChanged();
                    listener.onDoeClick(holder.mItem);
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

        public final TextView tv_name;

        public Doe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_name = (TextView) view.findViewById(R.id.tv_name);

        }
    }
}
