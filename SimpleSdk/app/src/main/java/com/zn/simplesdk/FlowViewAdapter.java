package com.zn.simplesdk;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.oversea.ads.api.ImageFlowView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlowViewAdapter extends RecyclerView.Adapter<FlowViewAdapter.ViewHolder> {
    static final int VIEWTYPE_INFO = android.R.layout.simple_list_item_1;
    static final int VIEWTYPE_ADS = R.layout.ad_flow_iten;
    private LayoutInflater mInflater;

    private Context mContext;

    private List<Item> mData = new ArrayList<>();

    private HashMap<Integer, ImageFlowView> mViewMap = new HashMap<>();
    public FlowViewAdapter(Activity context) {
        mContext = context;
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

    public void put(int type, ImageFlowView view) {
        mViewMap.put(type, view);
    }


    public List<Item> getData() {
        return mData;
    }

    @NonNull
    @Override
    public FlowViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FlowViewAdapter.ViewHolder viewHolder;
        switch (viewType) {
            case VIEWTYPE_INFO:
                viewHolder = new ViewHolder(mInflater.inflate(viewType, parent, false));
                break;
            default:
                viewHolder = new AdViewHolder(mViewMap.get(viewType));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlowViewAdapter.ViewHolder holder, int position) {
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

    }

    static class AdViewHolder extends ViewHolder {

        ImageFlowView imageView;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageFlowView) itemView;
        }

        @Override
        public void onBind(Item item) {
            Glide.with(itemView.getContext()).load(imageView.getUrl()).fitCenter().placeholder(R.drawable.default_image).error(R.drawable.default_image).into(imageView);
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
