package com.oversea.ads.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ZImageView extends ImageView {
    public static ZImageView create(Context context, ScaleType scaleType) {
        ZImageView flowView = new ZImageView(context);
        flowView.setScaleType(scaleType);
        return flowView;
    }

    public static ZImageView create(Context context, ScaleType scaleType, int width, int height) {
        ZImageView flowView = new ZImageView(context);
        flowView.setCustomSize(width, height);
        flowView.setScaleType(scaleType);
        return flowView;
    }

    private int customWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int customHeight = ViewGroup.LayoutParams.MATCH_PARENT;

    public ZImageView(Context context) {
        super(context);
        setScaleType(ScaleType.FIT_XY);
    }

    public void setCustomSize(int width, int height) {
        customWidth = width;
        customHeight = height;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            params.width = customWidth;
            params.height = customHeight;
        }
        super.setLayoutParams(params);
    }
}
