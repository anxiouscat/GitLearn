package com.oversea.ads.loaders;

import android.content.Context;

import com.oversea.ads.cfg.Cfg;

public class FlowLoader extends NativeAdsLoader {

    public FlowLoader(Context context, int requestCount) {
        super(context, Cfg.PLACEMENT_KEYGUARD, requestCount);
    }

    public FlowLoader(Context context) {
        this(context, 10);
    }

}
