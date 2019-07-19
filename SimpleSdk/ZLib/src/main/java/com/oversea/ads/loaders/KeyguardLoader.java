package com.oversea.ads.loaders;

import android.content.Context;
import android.nfc.Tag;

import com.oversea.ads.AdsSDK;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.cfg.Cfg;

public class KeyguardLoader extends NativeAdsLoader {

    public static KeyguardLoader create(Context context, String channel, String adPlaceId, int width, int height, int requestCount) {
        AdsSDK.init(context,channel);
        KeyguardLoader loader = new KeyguardLoader(context, channel, adPlaceId, width, height, requestCount);
        return loader;
    }

    private KeyguardLoader(Context context, String channel,String adPlaceId, int width, int height, int requestCount) {
        super(context, channel, adPlaceId, requestCount, width, height);
    }

    public void load(AdsLoaderListener listener) {
        setListener(listener);
        loadAds();
    }

    public void onDestroy () {
        AdsSDK.destroy();
    }

}

