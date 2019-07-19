package com.oversea.ads.polling;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.configmanagr.ConfigManager;
import com.oversea.ads.easyio.EasyHttp;
import com.oversea.ads.impl.NativeAdRequest;
import com.oversea.ads.impl.NativeAdResponse;
import com.oversea.ads.util.LogEx;
import com.oversea.ads.util.Util;

import java.util.Locale;

/**
 * Created by Bitao on 2016-08-23.
 */

public class PollingManager {
	private static final String TAG = "PollingManager";
    private static PollingManager sInstance = null;

    private static final int MAX_RETRY_CNT = 3;
    
    private static final int MSG_GET_URL = 0;
    private static final int MSG_POST_URL = 1;
    private static final int MSG_LOAD_CONFIG = 2;

    private HandlerThread mThread;
    private PollingHandler mHandler;
    private Context mContext = null;
    private String mChannel = null;

    private PollingManager() {
    }

    public static PollingManager getInstance() {
        if (sInstance == null) {
            sInstance = new PollingManager();
        }

        return sInstance;
    }

    public void setContext(Context context, String channel) {
        if (context == null) {
            return;
        }
        
        mChannel = channel;
        if (mContext != null) {
            return;
        }

        mContext = context;

        mThread = new HandlerThread("polling_thread");
        mThread.start();
        mHandler = new PollingHandler(mThread.getLooper());

        //mHandler.sendEmptyMessage(MSG_LOAD_CONFIG);
    }

    public void destroy() {
        if (mContext != null) {
			mHandler.removeMessages(MSG_LOAD_CONFIG);
            mHandler.removeMessages(MSG_GET_URL);
            mHandler.removeMessages(MSG_POST_URL);
			
            mThread.quit();
            mContext = null;
        }
    }

    public boolean sendAdEvent(String url) {
        if (mContext != null) {
            Message msg = mHandler.obtainMessage(MSG_GET_URL, url);
            msg.arg1 = 0;
            mHandler.sendMessage(msg);
            return true;
        }
        return false;
    }

    private class PollingHandler extends Handler {
        public PollingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        	if (mContext == null) {
        		return;
        	}
        	
            switch (msg.what) {
                case MSG_GET_URL:
                    String url = (String) msg.obj;
                    if (url == null || url.length() == 0) {
                        return;
                    }
                    
                    if (!Util.isConnected(mContext)) {
                    	Message retrymsg = Message.obtain(msg);
                    	sendMessageDelayed(retrymsg, 5 * 60 * 1000);
                    	return;
                    }
                    
                    int count = msg.arg1;
                    if (count < MAX_RETRY_CNT) {
                    	if (!doSendGet(url)) {
                    		count += 1;
                    		if (count < MAX_RETRY_CNT) {
                    			Message retrymsg = Message.obtain(msg);
                        		retrymsg.arg1 = count;
                        		sendMessageDelayed(retrymsg, 3 * 60 * 1000);
                    		}
                    	} 
                    }
                    
                    break;
                case MSG_POST_URL:
                    break;
                case MSG_LOAD_CONFIG:
                	removeMessages(MSG_LOAD_CONFIG);
                	if (!Util.isConnected(mContext) || !doLoadConfig()) {
                		mHandler.sendEmptyMessageDelayed(MSG_LOAD_CONFIG, 5 * 60 * 1000);
                	}
                    break;
            }
        }
    }

    private boolean doLoadConfig() {
        try {
        	LogEx.getInstance().i(TAG, "start doLoadConfig()");
            Locale locale = mContext.getResources().getConfiguration().locale;
            String clientID =  ConfigManager.getInstance().get("clientID");
            if (clientID == null) {
                clientID = "0";
            }
            //仅请求Config配置信息
            NativeAdRequest request = new NativeAdRequest(
                Util.isWifiConnected(mContext),
                Long.parseLong(clientID),
                locale.getCountry(),
                locale.getLanguage(),
                "",
                mChannel == null ? "" : mChannel,
                0,
                -1,
                -1);

            String url = Cfg.getServerUrl(mContext);
            NativeAdResponse response = EasyHttp.post(url, request, NativeAdResponse.CREATOR);
            ConfigManager.getInstance().put(response.configInfos);
            
            long nextpollingtime = 0;
            String pt = ConfigManager.getInstance().get("pollingtime");
            if (pt != null) {
				try {
					nextpollingtime = Long.valueOf(pt);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            if (nextpollingtime <= 0) {
        		nextpollingtime = 60 * 60 * 1000;
        	}
        	
        	mHandler.sendEmptyMessageDelayed(MSG_LOAD_CONFIG, nextpollingtime);
        	return true;
        } catch (Exception e) {
        	LogEx.getInstance().e(TAG, "doLoadConfig() failed!");
            e.printStackTrace();
        }
        
        return false;
    }

    private boolean doSendGet(String url) {
        try {
            EasyHttp.get(url, null, null);
            
            return true;
        } catch (Exception e) {
            LogEx.getInstance().e(TAG, "doSendGet() failed! url=" + url);
            e.printStackTrace();
        }
        
        return false;
    }
}
