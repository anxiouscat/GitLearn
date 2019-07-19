package com.oversea.ads.api;

import com.oversea.ads.base.AdBase;
import com.oversea.ads.base.AdListenerBase;
import com.oversea.ads.base.ImageBase;
import com.oversea.ads.impl.NativeAdInfo;

import java.util.List;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface NativeAd extends AdBase {
    public Object getObject();
    public void setAdListener(AdListenerBase listener);
    public void requestAdCoverImageSize(int width,int height);
    public String getAdSocialContext();
    public String getAdCallToAction();
    public String getAdTitle();
    public String getAdBody();
    public String getTemplateUrl();
    public ImageBase getAdIcon();
    public ImageBase getAdCoverImage();

    public void adShowing(boolean showing);

    public void onClickAction();

    public String getAction();

    public void pollingClickEvent();

}
