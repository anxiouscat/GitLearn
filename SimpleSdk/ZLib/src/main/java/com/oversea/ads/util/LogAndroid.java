package com.oversea.ads.util;

public class LogAndroid extends LogEx{
    
	static final String TAG = "ZAdSdk";
	
    private final static boolean mPrintDetail = false; // print controller
    
    private static String detail(String tag) {

        String print = "";
        boolean readNext = false;
        String localClassName = LogAndroid.class.getName();

        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stack) {
            String className = ste.getClassName();
            int filterSubClass;
            if ((filterSubClass = className.lastIndexOf('$')) > 0) {
                className = className.substring(0, filterSubClass);
            }

            if (localClassName.equals(className)) {
                readNext = true;
            } else if (readNext) {
                readNext = false;
                int classNameOffset = className.lastIndexOf(".");
                if (classNameOffset < 0) {
                    break;
                }
                print = ste.getClassName().substring(classNameOffset + 1); // print class name
                print += "." + ste.getMethodName(); // print method name
                print += "(" + ste.getLineNumber() + ")"; // print line number
            }
        }
        return print;
    }
    
    private static String p(String tag, String msg) {
        return (mPrintDetail ? "[" + detail(tag) + "]$ " : "") + (tag != null ? tag + ":/" : "") + msg;
    }
    
    @Override
    public void v(String msg) {
        v(null, msg);
    }

    @Override
    public void v(String tag, String msg) {
		if(!debug) {
			return;
		}
        android.util.Log.v(TAG, p(tag, msg));
    }

    @Override
    public void d(String msg) {
        d(null, msg);
    }

    @Override
    public void d(String tag, String msg) {
		if(!debug) {
			return;
		}
        android.util.Log.d(TAG, p(tag, msg));
    }

    @Override
    public void i(String msg) {
        i(null, msg);
    }

    @Override
    public void i(String tag, String msg) {
		if(!debug) {
			return;
		}
        android.util.Log.i(TAG, p(tag, msg));
    }

    @Override
    public void w(String msg) {
        w(null, msg);
    }

    @Override
    public void w(String tag, String msg) {
        android.util.Log.w(TAG, p(tag, msg));
    }
    
    @Override
    public void e(String msg) {
        e(null, msg);
    }

    @Override
    public void e(String tag, String msg) {
        android.util.Log.e(TAG, p(tag, msg));
    }
    
    /*
	public void e(String tag,String msg) {
		Log.e(TAG, tag + ":/" + msg);
	}

	@Override
	public void w(String tag, String msg) {
		Log.w(TAG, tag + ":/" + msg);
	}

	@Override
	public void d(String tag, String msg) {
		if(!debug) {
			return;
		}
		Log.d(TAG, tag + ":/" + msg);
	}
	*/
}
