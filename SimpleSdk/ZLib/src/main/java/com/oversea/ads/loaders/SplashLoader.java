package com.oversea.ads.loaders;

import android.content.Context;

import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdListenerBase;
import com.oversea.ads.base.ImageBase;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.configmanagr.ConfigManager;
import com.oversea.ads.impl.NativeAdImpl;
import com.oversea.ads.impl.NativeAdInfo;
import com.oversea.ads.util.LogEx;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public final class SplashLoader implements NativeAd, AdListenerBase {
	public static final String TAG = "NativeAdZk";
	NativeAd mNativeAd1;
	NativeAd mCurNativeAd;
	AdListenerBase mListener;
	Context mContext;
	String mPlacementId;
	String mChannel = Cfg.mChannel;
	String mLoadMode = Cfg.ADLOADMODE;

	public SplashLoader(Context context, String placementId, String loadMode) {
		mContext = context;
		mPlacementId = placementId;
		mLoadMode = ConfigManager.getInstance().getAdLoadMode(placementId, loadMode);

		mNativeAd1 = new NativeAdImpl(context, placementId);

		this.mCurNativeAd = this.mNativeAd1;
		this.mNativeAd1.setAdListener(this);

		setChannel(mChannel);
	}

	public SplashLoader(Context context, String placementId) {
		this(context, placementId, Cfg.ADLOADMODE);
	}

	SplashLoader(Context context, String placementId, NativeAd nativeAd) {
		mContext = context;
		mPlacementId = placementId;
		mNativeAd1 = nativeAd;
		mCurNativeAd = mNativeAd1;
		this.mNativeAd1.setAdListener(this);
	}

	@Override
	public Object getObject() {
		return mCurNativeAd;
	}

	@Override
	public void setAdListener(AdListenerBase listener) {
		this.mListener = listener;
	}

	@Override
	public void requestAdCoverImageSize(int width, int height) {
		this.mNativeAd1.requestAdCoverImageSize(width, height);
	}

	@Override
	public String getAdSocialContext() {
		return this.mCurNativeAd.getAdSocialContext();
	}

	@Override
	public String getAdCallToAction() {
		return this.mCurNativeAd.getAdCallToAction();
	}

	@Override
	public String getAdTitle() {
		return this.mCurNativeAd.getAdTitle();
	}

	@Override
	public String getAdBody() {
		return this.mCurNativeAd.getAdBody();
	}

	@Override
	public ImageBase getAdIcon() {
		return this.mCurNativeAd.getAdIcon();
	}

	@Override
	public ImageBase getAdCoverImage() {
		return this.mCurNativeAd.getAdCoverImage();
	}

	@Override
	public String getTemplateUrl() {
		return mCurNativeAd.getTemplateUrl();
	}

	@Override
	public void adShowing(boolean showing) {
		this.mCurNativeAd.adShowing(showing);
	}


	@Override
	public void pollingClickEvent() {
		mCurNativeAd.pollingClickEvent();
	}

	public void setChannel(String channel) {
		mChannel = channel;
		if (mNativeAd1 != null && mNativeAd1 instanceof NativeAdImpl) {
			((NativeAdImpl) mNativeAd1).setChannel(channel);
		}
	}

	void adClicked() {
		if (this.mCurNativeAd instanceof NativeAdImpl) {
			((NativeAdImpl) this.mCurNativeAd).adClicked();
		}
	}

	@Override
	public void loadAd() {
		this.mCurNativeAd.loadAd();
	}

	@Override
	public void destroy() {
		if(mNativeAd1 != null) {
			this.mNativeAd1.destroy();
		}
		this.mListener = null;
	}

	@Override
	public boolean isDestroy() {
		return mNativeAd1 == null || mNativeAd1.isDestroy();
	}

	@Override
	public String getPlacementId() {
		return mPlacementId;
	}


	@Override
	public String getAction() {
		return mCurNativeAd != null ? mCurNativeAd.getAction() : null;
	}

	@Override
	public void onError(NativeAd adBase, AdErrorBase adErrorBase) {
		LogEx.getInstance().d(TAG, "onError-");
		LogEx.getInstance()
				.d(TAG, " PlacementId == " + adBase.getPlacementId());
		LogEx.getInstance().d(
				TAG,
				adErrorBase.getErrorCode() + " "
						+ adErrorBase.getErrorMessage());
		if (this.mCurNativeAd == this.mNativeAd1 && isDestroy()) {
			this.mCurNativeAd.loadAd();
		} else {
			if (this.mListener == null) {
				return;
			}
			this.mListener.onError(adBase, adErrorBase);
		}
	}

	@Override
	public void onAdLoaded(NativeAd adBase) {
		LogEx.getInstance().d(TAG, "onAdLoaded-");
		LogEx.getInstance()
				.d(TAG, " PlacementId == " + adBase.getPlacementId());
		if (this.mListener == null) {
			return;
		}
		this.mListener.onAdLoaded(adBase);
	}


	@Override
	public void onClickAction() {

        adClicked();
	}

	@Override
	public String getUrl() {
		return getAdCoverImage().getUrl();
	}

	@Override
	public String getTitle() {
		return getAdTitle();
	}
}
