package com.oversea.ads.base;

import com.oversea.ads.api.NativeAd;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface NativeAdsManagerBase {
//    public NativeAdsManagerBase(Context context, String placementId, int count);
    Object getObject();
    void setListener(AdsLoaderListener listener);
    void requestAdsCoverImageSize(int width,int height);
    int getUniqueNativeAdCount();
    boolean isLoaded();
    void disableAutoRefresh();
    void loadAds();
    NativeAd nextNativeAd();
    String  getPlacementId();
    int getCount();
}
