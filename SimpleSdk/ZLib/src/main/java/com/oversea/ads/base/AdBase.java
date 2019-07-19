package com.oversea.ads.base;

import com.oversea.ui.entity.BaseInfo;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public interface AdBase extends BaseInfo {
    public void loadAd();
    public void destroy();
    public String getPlacementId();
    public boolean isDestroy();
}
