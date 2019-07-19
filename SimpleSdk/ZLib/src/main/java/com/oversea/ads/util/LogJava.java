package com.oversea.ads.util;

public class LogJava extends LogEx{

    @Override
    public void v(String msg) {
		if(!debug) {
			return;
		}
        System.out.println("v : " + LogAndroid.TAG + " : " + msg);
    }

    @Override
    public void v(String tag, String msg) {
		if(!debug) {
			return;
		}
        System.out.println("v : " + tag + " : " + msg);
    }

    @Override
    public void d(String msg) {
		if(!debug) {
			return;
		}
        System.out.println("d : " + LogAndroid.TAG + " : " + msg);
    }
    
    @Override
    public void d(String tag, String msg) {
		if(!debug) {
			return;
		}
        System.out.println("d : " + tag + " : " + msg);
    }

    @Override
    public void i(String msg) {
		if(!debug) {
			return;
		}
        System.out.println("i : " + LogAndroid.TAG + " : " + msg);
    }

    @Override
    public void i(String tag, String msg) {
		if(!debug) {
			return;
		}
        System.out.println("i : " + tag + " : " + msg);
    }

    @Override
    public void w(String msg) {
        // TODO Auto-generated method stub
        System.out.println("w : " + LogAndroid.TAG + " : " + msg);
    }
    
    @Override
    public void w(String tag, String msg) {
        System.out.println("w : " + tag + " : " + msg);
    }

    @Override
    public void e(String msg) {
        // TODO Auto-generated method stub
        System.out.println("e : " + LogAndroid.TAG + " : " + msg);
    }
    
    @Override
    public void e(String tag, String msg) {
        System.out.println("e : " + tag + " : " + msg);
    }
}
