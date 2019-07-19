package com.oversea.ads.cfg;

import android.content.Context;

import com.oversea.ads.configmanagr.ConfigManager;

import java.util.Locale;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class Cfg {
    public static String VERSION = "1";

    public static String URL_CN = "http://ad-sea.zookingsoft.com/AdsdkServer/query0";
    public static String URL_OAD = "http://ad-sea.zookingsoft.com/AdsdkServer/query0";

    /**
     * 开屏
     */
    public static String PLACEMENT_SPLASH = "41049";
    /**
     * Banner
     */
    public static String PLACEMENT_BANNER = "41051";
    /**
     * 信息流
     */
    public static String PLACEMENT_FLOW = "41050";

    /**
     * 锁屏 180*270
     */
    public static String PLACEMENT_KEYGUARD = "41074";

    /**
     * 金立
     * "41080"
     * "41079"
     * "41078"
     * "41077"
     */
    public static String PLACEMENT_GIONEE = "41080";

    /**
     * zkFirst;fbFirst,zkOnly
     */
    public static String ADLOADMODE = "zkFirst";
    public static String mChannel = null;

    public static void setChannel(String channel) {
    	mChannel = channel;
    }

    public static String getChannel() {
        return mChannel;
    }

    public static String getServerUrl(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String url = locale.equals(Locale.CHINA) ? URL_CN : URL_OAD;
    	String returl = ConfigManager.getInstance().get(url, url);
    	return returl;
    }
}
