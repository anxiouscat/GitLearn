package com.zn.simplesdk;

import android.app.Application;

import com.oversea.ads.AdsSDK;

public class ZApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AdsSDK.init(this, true);
        AdsSDK.setBrowserPackageName("com.android.mobilebrowser");
    }


}
