package com.zn.simplesdk.motion;


import com.oversea.ads.api.NativeAd;
import com.oversea.ads.easyio.ResDownloader.Task;

public class TemplateModel {
    private static final String TAG = "TemplateModel";

    static final int RES_MAX = 3;

    private EventListener mEventListener = null;

    private Task mTemplateTask;
    private Task mIconTask;
    private NativeAd mNativeAd;

    public TemplateModel(NativeAd adBase, Task templateTask, Task iconTask) {
        mNativeAd = adBase;
        this.mTemplateTask = templateTask;
        this.mIconTask = iconTask;
    }

    public NativeAd getAdInfo() {
        return mNativeAd;
    }

    public String getTemplatePath() {
        return mTemplateTask.data;
    }

    public String getIconPath() {
        return mIconTask.data;
    }

    public boolean isTaskComplete() {
        return mIconTask.complete && mTemplateTask.complete;
    }

    public boolean success() {
        return mIconTask.success && mTemplateTask.success;
    }

    public void onFinish(Task task) {
        task.complete = true;
    }

    public interface EventListener {
        void onLoad(long elapseTime);
        void onFailed(String msg);
    }

    public void setEventListener(EventListener listener) {
        mEventListener = listener;
    }

    public void loadAD() {
    }

    public void release() {
        //ResDownloader.safeClose(mDownloader);
    }
}
