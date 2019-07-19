package com.oversea.ads.util;

public abstract class LogEx {
	static LogEx logEx = null;
	static boolean debug = true;
	static {
		Class<?> androidLogClass = null;
		try {
			androidLogClass = Class.forName("android.util.Log");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(androidLogClass != null) {
			logEx = new LogAndroid();
		} else {
			logEx = new LogJava();
		}
	}
	public static LogEx getInstance() {
		return logEx;
	}
	
	public abstract void v(String msg);
	public abstract void v(String tag, String msg);
	public abstract void d(String msg);
	public abstract void d(String tag, String msg);
	public abstract void i(String msg);
	public abstract void i(String tag, String msg);
	public abstract void w(String msg);
	public abstract void w(String tag, String msg);
	public abstract void e(String msg);
	public abstract void e(String tag, String msg);
}
