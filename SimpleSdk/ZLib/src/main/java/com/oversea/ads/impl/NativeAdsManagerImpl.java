package com.oversea.ads.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.oversea.ads.AdsSDK;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.base.NativeAdsManagerBase;
import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.configmanagr.ConfigManager;
import com.oversea.ads.easyio.CacheFileManager;
import com.oversea.ads.easyio.EasyHttp;
import com.oversea.ads.easyio.ResDownloader;
import com.oversea.ads.util.LogEx;

import org.json.JSONObject;

import java.util.Locale;

import static com.oversea.ads.easyio.ResDownloader.Task.Type.ANIMATION;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdsManagerImpl implements NativeAdsManagerBase {
	public static final String TAG = "NativeAdsManagerImpl";
	private Context mContext;
	private String mPlacementId;
	private int mCount;
	private AdsLoaderListener mListener;
	private volatile boolean mIsLoaded = false;
	private NativeAdResponse mNativeAdResponse;
	private int mAdIndex = 0;
	private AdErrorImpl mAdError;
	private int mRequestWidth = -1;
	private int mRequestHeight = -1;
	private String mChannel = null;

	ResDownloader mDownloader;

	public NativeAdsManagerImpl(Context context, String placementId, int count) {
		this.mContext = context;
		this.mPlacementId = placementId;
		this.mCount = count;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mRequestWidth = dm.widthPixels;
        mRequestHeight = dm.heightPixels;
		mDownloader = new ResDownloader();
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public void setListener(AdsLoaderListener listener) {
		this.mListener = listener;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}

	@Override
	public void requestAdsCoverImageSize(int width, int height) {
		mRequestWidth = width;
		mRequestHeight = height;
	}

	@Override
	public int getUniqueNativeAdCount() {
		if (mNativeAdResponse == null) {
			return 0;
		}
		return mNativeAdResponse.count;
	}

	@Override
	public boolean isLoaded() {
		return mIsLoaded;
	}

	/**
	 * 暂时不管
	 */
	@Override
	public void disableAutoRefresh() {

	}

	@Override
	public void loadAds() {
		if (mIsLoaded) {
			return;
		}
		LogEx.getInstance().e(TAG, "loadAds-");
		if (AdsSDK.isCacheMode() && CacheFileManager.hasJsonCache(mPlacementId)) {
			mNativeAdResponse = new NativeAdResponse();
			boolean postCache = false;
			String json = CacheFileManager.readJson(mPlacementId);
			try {
				JSONObject jsonObject = new JSONObject(json);
				mNativeAdResponse.readFromJSON(jsonObject);
				ConfigManager.getInstance().put(mNativeAdResponse.configInfos);
				if (mNativeAdResponse.count > 0) {
					postOnAdsLoaded();
					postCache = true;
					return;
				}
			} catch (Throwable e) {

			} finally {
				if (postCache) {
					new Thread() {
						@Override
						public void run() {
							cacheRemoteJson();
						}
					}.start();
				}
			}

		}

		new Thread() {
			@Override
			public void run() {
				doRequest();
			}
		}.start();
	}

	void cacheRemoteJson() {
		LogEx.getInstance().e(TAG, "cacheRemoteJson-");
		mIsLoaded = true;
		Locale locale = this.mContext.getResources().getConfiguration().locale;
		String clientID = ConfigManager.getInstance().get("clientID");
		if (clientID == null) {
			clientID = "0";
		}
		NativeAdRequest request = new NativeAdRequest(isWifi(),
				Long.parseLong(clientID), locale.getCountry(),
				locale.getLanguage(), mPlacementId, mChannel, mCount,
				mRequestWidth, mRequestHeight);
		LogEx.getInstance().d(TAG, " request ==  " + request);
		try {
			String url = Cfg.getServerUrl(mContext);

			NativeAdResponse response = EasyHttp.post(url, request,
					NativeAdResponse.CREATOR);
			LogEx.getInstance().d(TAG,
					" response ==  " + response);

			CacheFileManager.writeJson(mPlacementId, response.toString());

			if(response.count > 0 && TextUtils.equals(Cfg.PLACEMENT_SPLASH, mPlacementId)) {
				NativeAd adBase = new NativeAdImpl(mContext, mPlacementId,
						response.nativeAdInfos.get(0));

				ResDownloader.Task task = new ResDownloader.Task(adBase.getTemplateUrl(), ANIMATION);
				ResDownloader.Task taskIcon = new ResDownloader.Task(adBase.getAdIcon().getUrl(), ResDownloader.Task.Type.IMAGE);
				ResDownloader.Task taskImage = new ResDownloader.Task(adBase.getUrl().toString(), ResDownloader.Task.Type.IMAGE);


				mDownloader.setResCallback(new ResDownloader.ResCallback() {
					@Override
					public void onFinish(ResDownloader downloader, ResDownloader.Task task) {
						LogEx.getInstance().d(TAG,
								" task.success: " + task.success + " path: " + task.url);
					}
				});

				if(!TextUtils.isEmpty(adBase.getTemplateUrl())) {
					mDownloader.execute(task, taskIcon);
				} else {
					mDownloader.execute(taskImage);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分线程执行中
	 */
	private void doRequest() {
		LogEx.getInstance().e(TAG, "run-");
		mIsLoaded = true;
		Locale locale = this.mContext.getResources().getConfiguration().locale;
		String clientID = ConfigManager.getInstance().get("clientID");
		if (clientID == null) {
			clientID = "0";
		}
		NativeAdRequest request = new NativeAdRequest(isWifi(),
				Long.parseLong(clientID), locale.getCountry(),
				locale.getLanguage(), mPlacementId, mChannel, mCount,
				mRequestWidth, mRequestHeight);
		LogEx.getInstance().d(TAG, " NativeAdRequest ==  " + request);
		try {
			String url = Cfg.getServerUrl(mContext);
			
			mNativeAdResponse = EasyHttp.post(url, request,
					NativeAdResponse.CREATOR);
			LogEx.getInstance().d(TAG,
					" NativeAdResponse ==  " + mNativeAdResponse);

			if (AdsSDK.isCacheMode()) {
				CacheFileManager.writeJson(mPlacementId, mNativeAdResponse.toString());
			}
			// 存储配置数据//
			ConfigManager.getInstance().put(mNativeAdResponse.configInfos);
			if (mNativeAdResponse.count > 0) {
				postOnAdsLoaded();
			} else {
				mAdError = AdErrorImpl.NO_FILL;
				postOnAdError(AdErrorImpl.NO_FILL);
			}
			return;
		} catch (Exception e) {
			mNativeAdResponse = null;
			LogEx.getInstance().e(TAG, "e == " + e);
			e.printStackTrace();
		}
		if (mNativeAdResponse == null) {
			mAdError = AdErrorImpl.SERVER_ERROR;
			postOnAdError(AdErrorImpl.SERVER_ERROR);
		}
	}

	@Override
	public synchronized NativeAd nextNativeAd() {
		if (mNativeAdResponse == null) {
			return null;
		}

		if (mNativeAdResponse.nativeAdInfos.size() == 0) {
			return null;
		}

		NativeAd nativeAd = new NativeAdImpl(mContext, mPlacementId,
				mNativeAdResponse.nativeAdInfos.get(this.mAdIndex));
		this.mAdIndex = (this.mAdIndex + 1)
				% this.mNativeAdResponse.nativeAdInfos.size();
		return nativeAd;
	}

	@Override
	public String getPlacementId() {
		return this.mPlacementId;
	}

	@Override
	public int getCount() {
		return this.mCount;
	}

	private void postOnAdError(final AdErrorImpl adError) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.onAdError(adError);
				}
			}
		});
		mIsLoaded = false;
	}

	private void postOnAdsLoaded() {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.onAdsLoaded(null);
				}
			}
		});
	}

	@SuppressLint("MissingPermission")
	public boolean isWifi() {
		ConnectivityManager connectMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiNetInfo.isConnected();
	}
}
