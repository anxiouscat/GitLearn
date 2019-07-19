package com.oversea.ads.configmanagr;

import android.content.Context;
import android.content.SharedPreferences;

import com.oversea.ads.util.LogEx;
import com.oversea.ads.util.SimpleStorageManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class ConfigManager {
	private static final String TAG = "ConfigManager";
	public static ConfigManager sInstance = null;
	public HashMap<String, String> mConfigInfos = new HashMap<String, String>();
	public Context mContext;
	public SimpleStorageManager mSimpleStorageManager;
	public boolean mLoaded = false;
	public HashMap<String, String> mLoadModeMap = new HashMap<String, String>();
	
	public static final String KEY_AD_LOAD_MODE_MAP = "loadModeMap";
	public static final String KEY_MAP = "modemap";
	public static final String KEY_ID = "id";
	public static final String KEY_MODE = "mode";

	private ConfigManager() {
		mSimpleStorageManager = SimpleStorageManager
				.getInstance("ConfigManager");
	}

	public synchronized static ConfigManager getInstance() {
		if (sInstance == null) {
			sInstance = new ConfigManager();
		}
		return sInstance;
	}

	public void setApplicationContext(Context context) {
		if (mContext != null) {
			return;
		}
		mContext = context;
		mSimpleStorageManager.setApplicationContext(mContext);
	}

	public synchronized String get(String key, String defValue) {
		String value = this.get(key);
		if (value == null) {
			return defValue;
		}
		return value;
	}

	public synchronized String get(String key) {
		if (!mLoaded) {
			load();
		}
		return mConfigInfos.get(key);
	}
	
	public synchronized String getAdLoadMode(String placementId, String defValue) {
		String value = mLoadModeMap.get(placementId);
		if (value == null) {
			return defValue;
		}
		
		return value;
	}

	public synchronized void put(ConfigInfos infos) {
		SharedPreferences sharedPreferences = mSimpleStorageManager
				.getSharedPreferences();
		if (sharedPreferences != null) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			HashMap<String, String> map = infos.getMap();
			for (String key : map.keySet()) {
				String value = map.get(key);
				if (value.equals(mConfigInfos.get(key))) {
					continue;
				}
				
				editor.putString(key, value);
				if (KEY_AD_LOAD_MODE_MAP.equals(key)) {
					genAdLoadModeMap(value);
					continue;
				}
				
				mConfigInfos.put(key, value);
			}
			editor.commit();
		}
	}

	public synchronized void put(String key, String value) {
		mConfigInfos.put(key, value);
		SharedPreferences sharedPreferences = mSimpleStorageManager
				.getSharedPreferences();
		sharedPreferences.edit().putString(key, value).commit();
	}

	public synchronized void load() {
		if (mLoaded) {
			return;
		}
		SharedPreferences sharedPreferences = mSimpleStorageManager
				.getSharedPreferences();
		if (sharedPreferences != null) {
			Map<String, ?> allData = sharedPreferences.getAll();
			for (String key : allData.keySet()) {
				String value = (String) allData.get(key);
				
				if (KEY_AD_LOAD_MODE_MAP.equals(key)) {
					genAdLoadModeMap(value);
					continue;
				}
				mConfigInfos.put(key, value);
			}
		}
		mLoaded = true;
	}

	public synchronized void postLoad() {
		new Thread() {
			public void run() {
				load();
			}
		}.start();
	}
	
	private synchronized void genAdLoadModeMap(String mapvalue) {
		try {
			JSONObject json = new JSONObject(mapvalue);
			
			if (json.has(KEY_MAP)) {
				JSONArray array = json.getJSONArray(KEY_MAP);
				if (array != null && array.length() > 0) {
					mLoadModeMap.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject jo = (JSONObject) array.get(i);
						String id = jo.getString(KEY_ID);
						String mode = jo.getString(KEY_MODE);
						
						mLoadModeMap.put(id, mode);
					}
				}
			}
			
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "genAdLoadModeMap() failed! mapstring=" + mapvalue);
			e.printStackTrace();
		}
	}
}
