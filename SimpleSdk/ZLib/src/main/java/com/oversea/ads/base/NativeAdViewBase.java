package com.oversea.ads.base;

import android.content.Context;
import android.view.View;

import com.oversea.ads.api.NativeAd;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface NativeAdViewBase {
    public View render(Context context, NativeAd nativeAdBase, NativeAdViewTypeBase nativeAdViewTypeBase);
}
