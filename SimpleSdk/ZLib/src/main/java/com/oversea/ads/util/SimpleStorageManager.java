package com.oversea.ads.util;

import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;

public class SimpleStorageManager {
	private static final String TAG = "SimpleStorageManager";
	private static final String FILE_NAME = "simple_pref_";
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	private static HashMap<String,SimpleStorageManager> mSimpleStorageManagerMap = new HashMap<String,SimpleStorageManager>();
	private String mIndex = "";
	public synchronized static SimpleStorageManager getInstance(String name) {
		if(mSimpleStorageManagerMap.get(name) == null) {
			SimpleStorageManager instance = new SimpleStorageManager();
			instance.mIndex = name;
			mSimpleStorageManagerMap.put(name,instance);
		}
		return mSimpleStorageManagerMap.get(name);
	}
	public synchronized static SimpleStorageManager getInstance() {
		return getInstance("");
	}
	public void setApplicationContext(Context context) {
		if(this.mContext != null ) {
			return;
		}
		this.mContext = context;
		mSharedPreferences = this.mContext.getSharedPreferences(FILE_NAME + mIndex, Context.MODE_PRIVATE);
	}
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
