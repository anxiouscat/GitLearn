package com.oversea.ads.loaders;

import android.content.Context;

import com.oversea.ads.AdsSDK;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.base.NativeAdsManagerBase;
import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.configmanagr.ConfigManager;
import com.oversea.ads.impl.NativeAdsManagerImpl;
import com.oversea.ads.util.LogEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdsLoader implements NativeAdsManagerBase, AdsLoaderListener {
	public static final String TAG = "NativeAdsManagerZk";
	private Context mContext;
	private String mPlacementId;
	private String mChannel = Cfg.getChannel();
	private int mCount;

	NativeAdsManagerBase mNativeAdsManager;
	AdsLoaderListener mListener;
	int mIndex = 0;

	boolean mNativeAdsManager1LoadSucccess = false;
	boolean mDisableAutoRefresh = false;
	int mRequestWidth = -1;
	int mRequestHeight = -1;
	String mLoadMode = Cfg.ADLOADMODE;

	private NativeAdsLoader(Context context,String channnel, String placementId, int count) {
		this.mContext = context;
		this.mPlacementId = placementId;
		this.mCount = count;
		this.mLoadMode = ConfigManager.getInstance().getAdLoadMode(placementId, mLoadMode);
		this.mNativeAdsManager = new NativeAdsManagerImpl(mContext,
				placementId, count);
		setChannel(channnel);
		this.mNativeAdsManager.setListener(this);
	}

	public Context getContext() {
		return mContext;
	}

	public NativeAdsLoader(Context context, String placementId, int count) {
		this(context,Cfg.getChannel(),placementId,count);
	}

	public NativeAdsLoader(Context context,String channel, String placementId, int count, int width, int height) {
		this(context,channel, placementId,count);
		setRequestSize(width, height);
	}

	public void setRequestSize(int width, int height) {
		mRequestWidth = width;
		mRequestHeight = height;
		mNativeAdsManager.requestAdsCoverImageSize(mRequestWidth, mRequestHeight);
	}

	@Override
	public void onAdsLoaded(List<NativeAd> items) {
		LogEx.getInstance().d(TAG, "onAdsLoaded-");
		LogEx.getInstance().d(TAG, " PlacementId == " + getPlacementId());
		if (this.mListener != null) {
			this.mListener.onAdsLoaded(getAllNativeAds());
		}
	}

	private void initState(NativeAdsManagerBase nativeAdsManager) {
		if (this.mDisableAutoRefresh) {
			nativeAdsManager.disableAutoRefresh();
		}
		nativeAdsManager.requestAdsCoverImageSize(mRequestWidth, mRequestHeight);
	}

	@Override
	public void onAdError(AdErrorBase adErrorBase) {
		LogEx.getInstance().d(TAG,
				"onAdError- , PlacementId == " + getPlacementId());
		LogEx.getInstance().d(
				TAG,
				"Errcode=" + adErrorBase.getErrorCode() + ";ErrMsg="
						+ adErrorBase.getErrorMessage());
		if (this.mListener != null) {
			this.mListener.onAdError(adErrorBase);
		}
	}

	@Override
	public Object getObject() {
		return this.mNativeAdsManager;
	}

	@Override
	public void setListener(AdsLoaderListener listener) {
		this.mListener = listener;
	}

	public void setChannel(String channel) {
		mChannel = channel;
		if (mNativeAdsManager instanceof NativeAdsManagerImpl) {
			((NativeAdsManagerImpl) mNativeAdsManager).setChannel(channel);
		}
	}

	@Override
	public void requestAdsCoverImageSize(int width, int height) {
		mRequestWidth = width;
		mRequestHeight = height;
		if (this.mNativeAdsManager != null) {
			this.mNativeAdsManager.requestAdsCoverImageSize(width, height);
		}
	}

	@Override
	public int getUniqueNativeAdCount() {
		int count = 0;
		if (this.mNativeAdsManager != null) {
			count += this.mNativeAdsManager.getUniqueNativeAdCount();
		}
		return count;
	}

	@Override
	public boolean isLoaded() {
		return this.mNativeAdsManager.isLoaded();
	}

	@Override
	public void disableAutoRefresh() {
		this.mDisableAutoRefresh = true;
		if (this.mNativeAdsManager != null) {
			this.mNativeAdsManager.disableAutoRefresh();
		}
	}

	@Override
	public void loadAds() {
		if (isLoaded()) {
			return;
		}
		this.mNativeAdsManager.loadAds();
	}

	@Override
	public NativeAd nextNativeAd() {
		NativeAd nativeAd = null;
		int count1 = 0;
		if (this.mNativeAdsManager != null) {
			count1 = this.mNativeAdsManager.getUniqueNativeAdCount();
		}
		if (mIndex < count1) {
			nativeAd = this.mNativeAdsManager.nextNativeAd();
		}
		mIndex = (mIndex + 1) % (count1);
		if (nativeAd == null) {
			return null;
		}
		return new SplashLoader(mContext, mPlacementId, nativeAd);
	}

	@Override
	public String getPlacementId() {
		return this.mPlacementId;
	}

	@Override
	public int getCount() {
		return this.mCount;
	}

	public List<NativeAd> getAllNativeAds() {
		if (getUniqueNativeAdCount() > 0) {
			List<NativeAd> array = new ArrayList<NativeAd>();

			if (this.mNativeAdsManager != null) {
				int cnt = mNativeAdsManager.getUniqueNativeAdCount();
				if (cnt > 0) {
					for (int i = 0; i < cnt; i++) {
						array.add(mNativeAdsManager.nextNativeAd());
					}
				}
			}
			return array;
		}
		return null;
	}
}
