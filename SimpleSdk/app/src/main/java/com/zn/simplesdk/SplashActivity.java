package com.zn.simplesdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.oversea.ads.api.AdListener;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.api.SplashView;

public class SplashActivity extends Activity implements AdListener {
    SplashView mSplashView;

    FrameLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_demo);
        mLayout = findViewById(R.id.frame);
        mSplashView = new SplashView(this);
        mSplashView.setAdListener(this);
        mSplashView.start(100);
        mSplashView.setTextFormat("跳过 (%ds)");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSplashView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSplashView.onResume();
    }

    @Override
    protected void onPause() {
        mSplashView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mSplashView.onDestroy();
        super.onDestroy();
    }


    void onJump() {
        startActivity(new Intent(this, FlowActivity.class));
        //startActivity(new Intent(this, BannerActivity.class));
    }

    public void jumpAndFinish() {
        onJump();
        onFinish();
    }

    public void onFinish() {
        finish();
    }

    /**
     * 应用跳转
     */
    @Override
    public void onAdJump() {
        onJump();
    }

    @Override
    public void onAdFailed() {
        jumpAndFinish();
    }

    @Override
    public boolean onAdLoading(NativeAd adBase) {
        Glide.with(this).load(adBase.getUrl()).into(mSplashView.getImageView());
        /**
         * 如果返回 false, 将采取默认加载方式
         */
        return true;
    }

    @Override
    public void onAdPresent() {
        mLayout.addView(mSplashView);
    }

    @Override
    public void onAdClose() {
        onFinish();
    }

}
