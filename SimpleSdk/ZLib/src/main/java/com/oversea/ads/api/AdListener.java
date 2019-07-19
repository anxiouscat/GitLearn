package com.oversea.ads.api;

public interface AdListener {
    public void onAdJump();
    public void onAdFailed();
    public void onAdPresent();
    public boolean onAdLoading(NativeAd adBase);
    public void onAdClose();
}
