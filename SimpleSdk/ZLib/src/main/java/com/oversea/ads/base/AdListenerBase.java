package com.oversea.ads.base;

import com.oversea.ads.api.NativeAd;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface AdListenerBase {
    public void onError(NativeAd adBase, AdErrorBase adErrorBase);
    public void onAdLoaded(NativeAd adBase);
}
