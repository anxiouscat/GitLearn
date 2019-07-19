package com.zn.simplesdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oversea.ads.api.BannerView;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.api.OnBannerLoader;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.loaders.KeyguardLoader;

import java.util.List;

public class BannerActivity extends Activity {

    private BannerView mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        initView();
        startLoad();
    }


    private void initView() {
        FrameLayout frameLayout = findViewById(R.id.banner_frame);
        mBanner = new BannerView(this);
        frameLayout.addView(mBanner);
        // 初始化
        mBanner.init(new OnBannerLoader() {
            @Override
            public void loadBanner(String url, ImageView imageView) {
                Glide.with(BannerActivity.this).load(url).placeholder(R.drawable.default_image).error(R.drawable.default_image).into(imageView);
            }
        });

    }

    private void loadBanner() {
        mBanner.load();
    }

    private void startLoad() {
        loadBanner();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
