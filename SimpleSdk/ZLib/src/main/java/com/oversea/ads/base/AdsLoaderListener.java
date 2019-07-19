package com.oversea.ads.base;

import com.oversea.ads.api.NativeAd;

import java.util.List;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface AdsLoaderListener {
    public void onAdsLoaded(List<NativeAd> items);
    public void onAdError(AdErrorBase adErrorBase);
}
