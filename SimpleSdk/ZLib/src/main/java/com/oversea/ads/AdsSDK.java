package com.oversea.ads;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.configmanagr.ConfigManager;
import com.oversea.ads.data.DeviceInfo;
import com.oversea.ads.easyio.CacheFileManager;
import com.oversea.ads.easyio.ResDownloader;
import com.oversea.ads.polling.PollingManager;
import com.oversea.ads.util.SimpleStorageManager;

import org.json.JSONArray;

import java.util.List;

public class AdsSDK {
	private static String TAG = "AdsSdk";

	public static Context mContext = null;

	private static String sDefaultBrowserPackageName = null;

	private static SimpleStorageManager sInstallStorageManager;

	private static InstallReceiver mInstallReceiver;

	static boolean isCacheMode = true;

	/**
	 * 如果isCacheMode值为true, 将优先显示缓存数据
	 * @param mode
	 */
	public static void setCacheMode(boolean mode) {
		isCacheMode = mode;
	}

	public static boolean isCacheMode() {
		return isCacheMode;
	}

	public static void setBrowserPackageName(String packageName) {
		sDefaultBrowserPackageName = packageName;
	}

	public static String getBrowserPackageName() {
		return sDefaultBrowserPackageName;
	}

	public static void init(Context appContext, String channel) {
		if (mContext != null) {
			return;
		}
		init(appContext, true);

		Cfg.setChannel(channel);
		PollingManager.getInstance().setContext(mContext, channel);
	}

	public static void init(Context appContext, boolean cacheMode) {
		if (mContext != null) {
			return;
		}
		setCacheMode(cacheMode);
		mContext = appContext.getApplicationContext();
		String metaChannel = getChannel(mContext);

		Cfg.setChannel(metaChannel);
		initDeviceInfo(mContext);
		ConfigManager.getInstance().setApplicationContext(mContext);
		ConfigManager.getInstance().postLoad();
		PollingManager.getInstance().setContext(mContext, metaChannel);
		CacheFileManager.getInstance().setContext(mContext, null);
		sInstallStorageManager = SimpleStorageManager.getInstance("install");
		sInstallStorageManager.setApplicationContext(mContext);
		mInstallReceiver = new InstallReceiver();
		mInstallReceiver.register(mContext);
	}

	public static void getImage(String remoteUrl, ResDownloader.ResCallback callback) {
		ResDownloader.Task task = new ResDownloader.Task(remoteUrl, ResDownloader.Task.Type.IMAGE);
		ResDownloader downloader = new ResDownloader();
		downloader.setResCallback(callback);
		downloader.execute(task);
	}


	public static void destroy() {
		if (mContext == null) {
			return;
		}
		PollingManager.getInstance().destroy();
		mInstallReceiver.unRegister(mContext);
		mContext = null;
		mInstallReceiver = null;
	}

	@SuppressLint("MissingPermission")
	private static void initDeviceInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String android_id = "";
		try {
			android_id = Settings.System.getString(
					context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
		}
		String deviceid = "";
		try {
			deviceid = tm.getDeviceId();
		} catch (Exception e) {
		}

		int screenw = context.getResources().getDisplayMetrics().widthPixels;
		int screenh = context.getResources().getDisplayMetrics().heightPixels;
		DeviceInfo.setDeviceInfo(deviceid, android_id,
				android.os.Build.VERSION.SDK_INT, android.os.Build.MODEL,
				screenw, screenh);
	}

	private static String getChannel(Context context) {
		String channel = null;
		try {
			Context ac = context.getApplicationContext();
			ApplicationInfo appInfo = ac.getPackageManager().getApplicationInfo(
					ac.getPackageName(),PackageManager.GET_META_DATA);
			channel = appInfo.metaData.getString("Z-CHANNEL");
			Cfg.setChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;
	}

	public static void onClickAction(String url, List<String> installEventUrls) {
		if (sInstallStorageManager != null && !TextUtils.isEmpty(url)) {
			try {
				String key = Uri.parse(url).getQueryParameter("id");
				JSONArray set = new JSONArray();

				for (String action: installEventUrls
					 ) {
					set.put(action);
				}
				Log.i(TAG, "key: " + key + " value: " + set.toString() + " url: " + url);
				final SharedPreferences sp = sInstallStorageManager.getSharedPreferences();
				sp.edit().putString(key, set.toString()).apply();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static class InstallReceiver extends BroadcastReceiver {

		public void register(Context context) {
			try {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
				intentFilter.addDataScheme("package");
				context.registerReceiver(this, intentFilter);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public void unRegister(Context context) {
			try {
				context.unregisterReceiver(this);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null) {
				try {
					String packageName = intent.getData().getSchemeSpecificPart();
					final SharedPreferences sp = sInstallStorageManager.getSharedPreferences();
					Log.i(TAG, "packageName: " + packageName);
					if (sp.contains(packageName)) {
						String json = sp.getString(packageName, "");
						sp.edit().remove(packageName).apply();

						JSONArray set = new JSONArray(json);
						for (int i = 0; i < set.length(); i++) {
							String url = set.getString(i);
							Log.i(TAG, "sendAdEvent: " + url);
							PollingManager.getInstance().sendAdEvent(url);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
}
