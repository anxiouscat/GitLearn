package com.oversea.ads.api;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.oversea.ui.XBanner;
import com.oversea.ui.XBannerUtils;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.loaders.BannerLoader;
import com.oversea.ads.loaders.NativeAdsLoader;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends XBanner {
    /**
     * 指示点位置
     */
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private List<NativeAd> mData = new ArrayList<>();

    public BannerView(Context context) {
        super(context);
        bind();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bind();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind();
    }

    private void bind() {
        /**
         * <com.oversea.ui.XBanner xmlns:android="http://schemas.android.com/apk/res/android"
         *     xmlns:app="http://schemas.android.com/apk/res-auto"
         *     android:id="@+id/banner"
         *     android:layout_width="match_parent"
         *     android:layout_height="250dp"
         *     app:AutoPlayTime="3000"
         *     app:isAutoPlay="false"
         *     app:isShowIndicatorOnlyOne="true"
         *     app:isShowTips="true"
         *     app:isHandLoop="true"
         *     app:pageChangeDuration="800"
         *     app:pointsContainerBackground="#442e2e2e"
         *     app:pointsPosition="CENTER"
         *     app:pointsVisibility="true"
         *     app:tipTextSize="12sp" />
         */
        setShowIndicatorOnlyOne(true);
        setAutoPalyTime(2500);
        setAutoPlayAble(true);
        setHandLoop(true);
        setPageChangeDuration(800);
        setPointContainerBackgroundDrawable(new ColorDrawable(Color.parseColor("#442e2e2e")));
        setPointPosition(RIGHT);
        setPointsIsVisible(true);
        setTipTextSize(XBannerUtils.sp2px(getContext(), 12));

    }

    @Override
    public void setShowIndicatorOnlyOne(boolean showIndicatorOnlyOne) {
        super.setShowIndicatorOnlyOne(showIndicatorOnlyOne);
    }

    @Override
    public void setAutoPalyTime(int mAutoPalyTime) {
        super.setAutoPalyTime(mAutoPalyTime);
    }

    @Override
    public void setAutoPlayAble(boolean mAutoPlayAble) {
        super.setAutoPlayAble(mAutoPlayAble);
    }

    @Override
    public void setHandLoop(boolean handLoop) {
        super.setHandLoop(handLoop);
    }

    @Override
    public void setPageChangeDuration(int duration) {
        super.setPageChangeDuration(duration);
    }

    @Override
    public void setPointContainerBackgroundDrawable(Drawable backgroundDrawable) {
        super.setPointContainerBackgroundDrawable(backgroundDrawable);
    }

    @Override
    public void setPointPosition(int position) {
        super.setPointPosition(position);
    }

    @Override
    public void setPointsIsVisible(boolean isVisible) {
        super.setPointsIsVisible(isVisible);
    }

    @Override
    public void setTipTextSize(int tipTextSize) {
        super.setTipTextSize(tipTextSize);
    }

    public void init(final OnBannerLoader loader) {
        setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                NativeAd adBase = (NativeAd) model;
                adBase.onClickAction();
            }
        });
        loadImage(new XBannerAdapter(){
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                NativeAd adBase = (NativeAd) model;
                adBase.adShowing(true);
                String url = adBase.getAdCoverImage().getUrl();
                loader.loadBanner(url, (ImageView) view);
            }
        });
    }

    public void load() {
        final NativeAdsLoader adsLoader = new BannerLoader(getContext());
        adsLoader.setListener(new AdsLoaderListener() {

            @Override
            public void onAdsLoaded(List<NativeAd> items) {
                final List<NativeAd> ads =  items;
                mData.clear();
                mData.addAll(ads);
                setBannerData(ads);
            }

            @Override
            public void onAdError(AdErrorBase adErrorBase) {

            }
        });
        adsLoader.loadAds();
    }

    public List<NativeAd> getData() {
        return mData;
    }
}
