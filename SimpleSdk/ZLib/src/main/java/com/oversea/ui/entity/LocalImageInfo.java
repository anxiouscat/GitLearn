package com.oversea.ui.entity;


import com.oversea.ui.supportx.DrawableRes;

/**
 * author: xiaohaibin.
 * time: 2018/12/3
 * mail:xhb_199409@163.com
 * github:https://github.com/xiaohaibin
 * describe: 本地资源图片
 */
public class LocalImageInfo extends SimpleBannerInfo {

    @DrawableRes
    private int bannerRes;

    public LocalImageInfo(int bannerRes) {
        this.bannerRes = bannerRes;
    }

    @Override
    public Integer getUrl() {
        return bannerRes;
    }
}
