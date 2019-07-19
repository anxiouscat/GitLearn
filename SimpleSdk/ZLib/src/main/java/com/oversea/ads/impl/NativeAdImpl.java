package com.oversea.ads.impl;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.oversea.ads.AdsSDK;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdListenerBase;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.base.ImageBase;
import com.oversea.ads.easyio.CacheFileManager;
import com.oversea.ads.polling.PollingManager;
import com.oversea.ads.util.ContextUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdImpl implements NativeAd, AdsLoaderListener {
	Context mContext;
	String mPlacementId;
	AdListenerBase mListener;
	HashSet<View> mViewForInteractionSet = new HashSet<View>();
	NativeAdInfo mNativeAdInfo = new NativeAdInfo();
	NativeAdsManagerImpl mNativeAdsManagerImpl = null;
	boolean mIsShowing = false;
	private PollingShowEventHandler mDelayPollingHandler = null;
	private String mChannel;

	public NativeAdImpl() {
	}

	public NativeAdImpl(NativeAdImpl nativeAdImpl) {
		this.clone(nativeAdImpl);
	}

	public NativeAdImpl(Context context, String placementId) {
		this.mContext = context;
		this.mPlacementId = placementId;
	}

	public NativeAdImpl(Context context, String placementId,
			NativeAdInfo nativeAdInfo) {
		this.mContext = context;
		this.mPlacementId = placementId;
		this.mNativeAdInfo = nativeAdInfo;
	}

	public void clone(NativeAdImpl nativeAdImpl) {
		this.mContext = nativeAdImpl.mContext;
		this.mPlacementId = nativeAdImpl.mPlacementId;
		this.mNativeAdInfo = nativeAdImpl.mNativeAdInfo;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public void setAdListener(AdListenerBase listener) {
		this.mListener = listener;
	}

	@Override
	public void requestAdCoverImageSize(int width, int height) {
		mNativeAdsManagerImpl.requestAdsCoverImageSize(width, height);
	}

	@Override
	public String getAdSocialContext() {
		return mNativeAdInfo.adSocialContext;
	}

	@Override
	public String getAdCallToAction() {
		return mNativeAdInfo.adCallToAction;
	}

	@Override
	public String getAdTitle() {
		return mNativeAdInfo.adTitle;
	}

	@Override
	public String getAdBody() {
		return mNativeAdInfo.adBody;
	}

	@Override
	public ImageBase getAdIcon() {
		return mNativeAdInfo.adIcon;
	}

	@Override
	public ImageBase getAdCoverImage() {
		return mNativeAdInfo.adCoverImage;
	}

	@Override
	public String getTemplateUrl() {
		return mNativeAdInfo.templateUrl;
	}

	@Override
	public void adShowing(boolean showing) {
		if (showing) {
			if (mIsShowing) {
				return;
			}
			mIsShowing = showing;
			int time = mNativeAdInfo.getNextShowCheckTime(-1);
			if (time >= 0) {
				if (mDelayPollingHandler == null) {
					mDelayPollingHandler = new PollingShowEventHandler();
				}

				Message msg = mDelayPollingHandler.obtainMessage(0, time, 0);
				mDelayPollingHandler.sendMessageDelayed(msg, time);
			}
		} else {
			if (!mIsShowing) {
				return;
			}
			mIsShowing = showing;
			if (mDelayPollingHandler != null) {
				mDelayPollingHandler.removeMessages(0);
			}
		}
	}

	public void setChannel(String channel) {
		mChannel = channel;
	}

	public void adClicked() {
		pollingClickEvent();
	}

	@Override
	public void loadAd() {
		mNativeAdsManagerImpl = new NativeAdsManagerImpl(mContext,
				mPlacementId, 1);
		mNativeAdsManagerImpl.setChannel(mChannel);
		mNativeAdsManagerImpl.loadAds();
		mNativeAdsManagerImpl.setListener(this);
	}

	@Override
	public void destroy() {
		mNativeAdsManagerImpl = null;
		if (mDelayPollingHandler != null) {
			mDelayPollingHandler.removeMessages(0);
		}
	}

	@Override
	public boolean isDestroy() {
		return mNativeAdsManagerImpl == null;
	}

	@Override
	public String getPlacementId() {
		return this.mPlacementId;
	}


	@Override
	public void onClickAction() {
		executeIntent();
		pollingClickEvent();
	}

	@Override
	public String getAction() {
		return mNativeAdInfo == null ? null : mNativeAdInfo.intent;
	}

	public void executeIntent() {
		if (mNativeAdInfo.intent != null
				&& mNativeAdInfo.intent.length() > 0) {

			try {
				ContextUtil.startAction(mContext, mNativeAdInfo.intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void pollingClickEvent() {
		if (mNativeAdInfo != null && mNativeAdInfo.mClickEventUrls.size() > 0) {
			for (String url : mNativeAdInfo.mClickEventUrls) {
				AdsSDK.onClickAction(mNativeAdInfo.intent, mNativeAdInfo.getInstalledEventUrls());
				PollingManager.getInstance().sendAdEvent(url);
			}
		}
	}

	@Override
	public void onAdsLoaded(List<NativeAd> items) {
		NativeAdImpl nativeAd = (NativeAdImpl) mNativeAdsManagerImpl
				.nextNativeAd();
		this.clone(nativeAd);
		if (mListener != null) {
			mListener.onAdLoaded(this);
		}
	}

	@Override
	public void onAdError(AdErrorBase adErrorBase) {
		AdErrorImpl adError = (AdErrorImpl) adErrorBase;
		if (mListener != null) {
			mListener.onError(this, adErrorBase);
		}
	}

	private void findViews(HashSet<View> viewSet, View view) {
		if (view.isClickable()) {
			viewSet.add(view);
		}
		if (view instanceof ViewGroup) {
			int count = ((ViewGroup) view).getChildCount();
			for (int i = 0; i < count; i++) {
				View child = ((ViewGroup) view).getChildAt(i);
				findViews(viewSet, child);
			}
		}
	}

	@Override
	public String getUrl() {
		return getAdCoverImage().getUrl();
	}

	@Override
	public String getTitle() {
		return getAdTitle();
	}

	private class PollingShowEventHandler extends android.os.Handler {

		public PollingShowEventHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			if (mNativeAdInfo == null) {
				return;
			}
			int time = msg.arg1;
			ArrayList<String> urls = mNativeAdInfo.getShowPollingUrls(time);
			if (urls != null && urls.size() > 0) {
				for (String url : urls) {
					if (url != null && url.length() > 0) {
						PollingManager.getInstance().sendAdEvent(url);
					}
				}
			}

			int nexttime = mNativeAdInfo.getNextShowCheckTime(time);
			if (nexttime >= 0) {
				int delay = nexttime - time;
				if (delay >= 0) {
					Message m = obtainMessage(0, nexttime, 0);
					sendMessageDelayed(m, delay);
				}
			}
		}
	}
}
