package com.ypunval.pcbang.adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.util.Constant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NearRVA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private final List<PCBang> mValues;
    private final PCBangListenerInterface.OnNearByClickListener listener;

    ViewGroup parentViewGroup;

    public NearRVA(List<PCBang> items, PCBangListenerInterface.OnNearByClickListener listener) {
        mValues = items;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 2;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == mValues.size() + 1)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentViewGroup = parent;
        View view = null;
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_nearby, parent, false);
                return new HeaderViewHolder(view);
            case TYPE_ITEM:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_nearby_item, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_nearby_item_pre_5, parent, false);
                }
                return new ItemViewHolder(view);
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_nearby, parent, false);
                return new FooterViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.ll_conveniences.removeAllViews();
            int margin = (int) parentViewGroup.getContext().getResources().getDimension(R.dimen.theme_margin);
            int padding = (int) parentViewGroup.getContext().getResources().getDimension(R.dimen.theme_padding);
            int height = (int) parentViewGroup.getContext().getResources().getDimension(R.dimen.theme_height);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
            params.setMargins(margin, 0, 0, 0);

            for (int i = 0; i < Constant.conveniences.size(); i++) {
                final Convenience convenience = Constant.conveniences.get(i);
                TextView textView = (TextView) LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.item_selected_convenience, parentViewGroup, false);
                textView.setText(convenience.getName());
                textView.setLayoutParams(params);
                textView.setPadding(padding, 0, padding, 0);
                headerViewHolder.ll_conveniences.addView(textView);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onNearByHeaderAddConvenience(convenience);
                    }
                });
            }

            if (Constant.selectedConveniences.size() == 0) {
                headerViewHolder.ll_selected_container.setVisibility(View.GONE);
            } else {
                headerViewHolder.ll_selected_container.setVisibility(View.VISIBLE);
                headerViewHolder.ll_selected_conveniences.removeAllViews();
                for (int i = 0; i < Constant.selectedConveniences.size(); i++) {
                    final Convenience convenience = Constant.selectedConveniences.get(i);
                    TextView textView = (TextView) LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.item_selected_convenience, parentViewGroup, false);
                    textView.setText(Constant.selectedConveniences.get(i).getName());
                    textView.setLayoutParams(params);
                    textView.setPadding(padding, 0, padding, 0);
                    headerViewHolder.ll_selected_conveniences.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onNearByHeaderRemoveConvenience(convenience);
                        }
                    });
                }
            }


        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            itemViewHolder.mItem = mValues.get(position-1);
            PCBang pcBang = itemViewHolder.mItem;
            itemViewHolder.tv_title.setText(pcBang.getName());
            itemViewHolder.tv_rate.setText(pcBang.getAverageRate() + "");
            itemViewHolder.tv_review_count.setText(pcBang.getReviewCount() + "");
            itemViewHolder.tv_left_seat.setText(pcBang.getLeftSeat() + "");
            itemViewHolder.tv_total_seat.setText(pcBang.getTotalSeat() + "");
            itemViewHolder.tv_address.setText(pcBang.getAddress1());
            if (itemViewHolder.mItem.getMinPrice() == 1000000)
                itemViewHolder.tv_min_price.setText("?");
            else
                itemViewHolder.tv_min_price.setText(pcBang.getMinPrice() + "");
            float km = pcBang.getDistance();
            String dist = "";
            if (km < 1) {
                int i_meter = (int) (km * 1000);
                dist = i_meter + "m";
            } else {
                dist = String.format("%.1f", km) + "km";
            }
            itemViewHolder.tv_distance.setText(dist);

            itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != listener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        listener.onNearByItemClick(itemViewHolder.mItem);
                    }
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.btnMore.setText(Constant.rangeKm + 3 + "km 더보기");
            footerViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.rangeKm += 3;
                    listener.onNearByFooterClick();
                }
            });
        }
    }

    public void setHeaderUIUpdate() {

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.iv_image)
        ImageView iv_image;
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_rate)
        TextView tv_rate;
        @Bind(R.id.tv_review_count)
        TextView tv_review_count;
        @Bind(R.id.tv_left_seat)
        TextView tv_left_seat;
        @Bind(R.id.tv_total_seat)
        TextView tv_total_seat;
        @Bind(R.id.tv_address)
        TextView tv_address;
        @Bind(R.id.tv_distance)
        TextView tv_distance;
        @Bind(R.id.tv_min_price)
        TextView tv_min_price;
        public PCBang mItem;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_conveniences)
        LinearLayout ll_conveniences;
        @Bind(R.id.ll_selected_conveniences)
        LinearLayout ll_selected_conveniences;
        @Bind(R.id.ll_selected_container)
        LinearLayout ll_selected_container;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btnMore)
        android.widget.Button btnMore;

        public FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
