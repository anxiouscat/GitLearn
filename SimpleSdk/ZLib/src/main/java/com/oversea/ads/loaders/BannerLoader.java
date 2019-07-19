package com.oversea.ads.loaders;

import android.content.Context;

import com.oversea.ads.cfg.Cfg;

public class BannerLoader extends NativeAdsLoader {

    public BannerLoader(Context context, int requestCount) {
        super(context, Cfg.PLACEMENT_BANNER, requestCount);
    }

    public BannerLoader(Context context) {
        this(context, 10);
    }
}
