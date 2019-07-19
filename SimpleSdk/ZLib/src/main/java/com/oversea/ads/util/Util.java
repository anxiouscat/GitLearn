package com.oversea.ads.util;

import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Util {
	public static final String TAG = "Util";

	public static String UA;

	public  static void safeClose(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void killProcess(Context context,String[] processNames) {
		try {
			ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
			for(String processName:processNames) {
				for (RunningAppProcessInfo info : processes) {
					if (info.processName.equals(processName)) {
						android.os.Process.killProcess(info.pid);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static String bytes2HexString(byte[] b) {  
    	String ret = "";  
    	for (int i = 0; i < b.length; i++) {   
    		String hex = Integer.toHexString(b[i] & 0xFF);  
    		if (hex.length() == 1) {
    			hex = '0' + hex;   
    		}  
    		ret += hex;  
    	}  
    	return ret; 
    }
    public static String getMD5Digest(String data) {
    	try {
			byte[] digest = getMD5Digest(data.getBytes("utf-8"));
			String digestStr = bytes2HexString(digest);
			return digestStr;
		} catch (Exception e) {
		}
    	return data;
    }
    
    public static byte[] getMD5Digest(byte[] data) {
    	try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(data);
			return digest;
		} catch (Exception e) {
		}
    	return data;
    }
    
    public static boolean isConnected(Context context) {
    	try {
			ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfo = connectMgr.getActiveNetworkInfo();
			if (netinfo == null || !netinfo.isConnected()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public static boolean isWifiConnected(Context context) {
    	try {
			ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfo = connectMgr.getActiveNetworkInfo();
			if (netinfo != null && netinfo.isConnected() && netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return false;
    }

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		return dm.widthPixels;
	}



	public static String getUserAgent() {
		try {
			if (UA == null) {
				String ua = System.getProperty("http.agent");
				if (ua != null && ua.length() > 0) {
					UA =  ua;
				} else {
					StringBuffer buffer = new StringBuffer();
					// Add version
					final String version = Build.VERSION.RELEASE;
					if (version.length() > 0) {
						buffer.append(version);
					} else {
						// default to "1.0"
						buffer.append("1.0");
					}
					buffer.append("; ");

					// add the model for the release build
					final String model = Build.MODEL;
					if (model.length() > 0) {
						buffer.append(model);
					}

					final String id = Build.ID;
					if (id.length() > 0) {
						buffer.append(" Build/");
						buffer.append(id);
					}

					String dv = System.getProperty("java.vm.version");
					if (dv == null || dv.length() == 0) {
						dv = "2.1.0";
					}

					UA = "Dalvik/" + dv + " (Linux; U; Android " + buffer.toString() + ")";
				}
			}
		} catch (Throwable e) {
			UA = "";
		}

		return UA;
	}

}
