package com.zn.simplesdk;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.oversea.ads.api.NativeAd;

import java.util.ArrayList;
import java.util.List;

public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.ViewHolder> {
    static final int VIEWTYPE_INFO = android.R.layout.simple_list_item_1;
    static final int VIEWTYPE_ADS = R.layout.ad_flow_iten;
    private LayoutInflater mInflater;

    private List<Item> mData = new ArrayList<>();
    public FlowAdapter(Activity context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    public void setData(List<Item> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<Item> getData() {
        return mData;
    }

    @NonNull
    @Override
    public FlowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FlowAdapter.ViewHolder viewHolder;
        switch (viewType) {
            case VIEWTYPE_ADS:
                viewHolder = new AdViewHolder(mInflater.inflate(viewType, parent, false));
                break;
            default:
                viewHolder = new ViewHolder(mInflater.inflate(viewType, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlowAdapter.ViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView instanceof TextView) {
                textView = (TextView) itemView;
            }
        }

        public void onBind(Item item) {
            String text = item.getData();
            textView.setText(text);
        }

        public void onUnBind() {

        }
    }

    static class AdViewHolder extends ViewHolder implements View.OnClickListener {

        NativeAd mNativeAdBase;
        ImageView imageView;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onBind(Item item) {
            mNativeAdBase = item.getData();
            mNativeAdBase.adShowing(true);
            Glide.with(itemView.getContext()).load(mNativeAdBase.getUrl()).fitCenter().placeholder(R.drawable.default_image).error(R.drawable.default_image).into(imageView);
        }

        @Override
        public void onClick(View v) {
            mNativeAdBase.onClickAction();
        }
    }

    public static class Item {
        public int type;
        public Object data;
        public Item(int type, Object data) {
            this.type = type;
            this.data = data;
        }

        public <T> T getData() {
            return (T)data;
        }
    }

}
