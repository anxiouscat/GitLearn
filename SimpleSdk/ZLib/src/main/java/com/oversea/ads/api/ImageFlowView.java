package com.oversea.ads.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ImageFlowView extends ImageView implements View.OnClickListener {
    public static ImageFlowView create(Context context, ScaleType scaleType) {
        ImageFlowView flowView = new ImageFlowView(context);
        flowView.setScaleType(scaleType);
        return flowView;
    }

    public static ImageFlowView create(Context context, ScaleType scaleType, int width, int height) {
        ImageFlowView flowView = new ImageFlowView(context);
        flowView.setCustomSize(width, height);
        flowView.setScaleType(scaleType);
        return flowView;
    }

    private int customWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int customHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    private NativeAd mNativeAd;

    public ImageFlowView(Context context) {
        super(context);
        setScaleType(ScaleType.FIT_XY);
    }

    public void setNativeAd(NativeAd ad) {
        setOnClickListener(this);
        mNativeAd = ad;
    }

    public String getUrl() {
        if (mNativeAd != null) {
            return mNativeAd.getAdCoverImage().getUrl();
        }
        return null;
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

    @Override
    public void onClick(View v) {
        if (mNativeAd != null) {
            mNativeAd.onClickAction();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if (mNativeAd != null) {
            mNativeAd.adShowing(true);
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mNativeAd != null) {
            mNativeAd.adShowing(false);
        }
        super.onDetachedFromWindow();
    }
}
