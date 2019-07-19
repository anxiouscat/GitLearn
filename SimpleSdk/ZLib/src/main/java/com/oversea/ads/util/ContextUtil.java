package com.oversea.ads.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.oversea.ads.AdsSDK;

public class ContextUtil {
    static String PKG_GOOGLEPLAY = "com.android.vending";
    public static String getPlayPackageName() {
        return PKG_GOOGLEPLAY;
    }

    public static boolean isAppInstalled(Context cxt, String packageName) {
        try {
            ApplicationInfo info = cxt.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPlayInstalled(Context context) {
        return isAppInstalled(context, PKG_GOOGLEPLAY);
    }

    public static void startAction(Context context, String uri) throws Exception {
        try {
            if(ContextUtil.isPlayInstalled(context)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage(ContextUtil.getPlayPackageName());
                context.startActivity(intent);
            } else {
                Intent intent = Intent.parseUri(uri, 0);
                if(!TextUtils.isEmpty(AdsSDK.getBrowserPackageName())) {
                    intent.setPackage(AdsSDK.getBrowserPackageName());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            Intent intent = Intent.parseUri(uri, 0);
            if(!TextUtils.isEmpty(AdsSDK.getBrowserPackageName())) {
                intent.setPackage(AdsSDK.getBrowserPackageName());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
