package com.oversea.ads.easyio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.oversea.ads.util.LogEx;

import java.io.File;

/**
 * 资源下载
 */
public class ResDownloader {


    public interface ResCallback {
        void onFinish(ResDownloader downloader, Task task);
    }

    public enum DownloadMode{
        ONE_BY_ONE,
        MULTIPLE,
    }
    private DownloadWorker[] mDownloadWorkers = null;
    private DownloadMode mMode = DownloadMode.MULTIPLE;
    private ResCallback mEventListener = null;
    private Handler mHandler = new ResHandler();
    private long mElapse = 0;

    public long getElapse() {
        return mElapse;
    }

    public ResDownloader setMode(DownloadMode mode) {
        mMode = mode;
        return this;
    }

    public ResDownloader setResCallback(ResCallback listener) {
        mEventListener = listener;
        return this;
    }

    public void destroy() {
    }


    public void execute(Task... tasks) {
        if (tasks == null || tasks.length <= 0) {
            return;
        }
        if (mDownloadWorkers != null) {
            return;
        }

        // 忽略掉数组中的空值
        int count = 0;
        for (Task task : tasks) {
            if (task != null) {
                count++;
            }
        }
        Task[] newTasks = tasks;
        if (count != tasks.length) {
            newTasks = new Task[count];
            int index = 0;
            for (Task task : tasks) {
                if (task != null) {
                    newTasks[index++] = task;
                }
            }
        }
        tasks = null;
        if (newTasks.length <= 0) {
            return;
        }

        if (mMode == DownloadMode.MULTIPLE) {
            mDownloadWorkers = new DownloadWorker[newTasks.length];
            for (int i = 0; i < newTasks.length; ++i) {
                mDownloadWorkers[i] = new DownloadWorker(mHandler, new Task[] {newTasks[i]});
            }
        } else if (mMode == DownloadMode.ONE_BY_ONE) {
            mDownloadWorkers = new DownloadWorker[1];
            mDownloadWorkers[0] = new DownloadWorker(mHandler, newTasks);
        }
        if (mDownloadWorkers != null) {
            for (DownloadWorker worker : mDownloadWorkers) {
                worker.start();
            }
        }
    }

    public static ResDownloader safeClose(ResDownloader downloader) {
        try {
            if (downloader != null && downloader.mDownloadWorkers != null) {
                for (DownloadWorker worker : downloader.mDownloadWorkers) {
                    try {
                        worker.interrupt();
                    } catch (Throwable ee) {
                        // NOOP
                    }
                }
                downloader.mDownloadWorkers = null;
            }
        } catch (Throwable e) {
            // NOOP
        }

        return null;
    }

    private static class DownloadWorker extends Thread {
        private Task[] mTasks;
        private Handler mHandler;
        DownloadWorker(Handler handler, Task[] tasks) {
            mTasks = tasks;
            mHandler = handler;
        }

        @Override
        public void run() {
            try {
                if (mTasks == null || mTasks.length <= 0 || mHandler == null) {
                    return;
                }
                for (int i = 0; i < mTasks.length; ++i) {
                    Task task = mTasks[i];
                    String path = null;
                    if (task != null) {
                        LogEx.getInstance().i("Start Download " + task);
                        long startTime = System.currentTimeMillis();
                        if (task.type == Task.Type.IMAGE) {
                            path = loadImage(task);
                        } else if (task.type == Task.Type.ANIMATION) {
                            path = loadAnimRes(task);
                        } else if (task.type == Task.Type.VIDEO) {
                            path = loadVideo(task);
                        }
                        task.success = !TextUtils.isEmpty(path);
                        task.data = path;
                        task.elapse = System.currentTimeMillis() - startTime;
                        mHandler.sendMessage(mHandler.obtainMessage(ResHandler.MSG_TASK_FINISH, task));
                        LogEx.getInstance().i("Finish Download " + task.network_elapse + " " + task.elapse + " " + task);
                    }
                }
            } catch (Exception e) {
            }
        }
        private boolean download(Task task) {
            File savedFile = CacheFileManager.getInstance().getCacheTmpFile(task.url);
            Util.safeDeleteFile(savedFile);
            long startTime = System.currentTimeMillis();
            if (!EasyHttp.downloadFile(task.url, null, savedFile)){
                Util.safeDeleteFile(savedFile);
                task.error = "Download failed";
                return false;
            }
            task.network_elapse = System.currentTimeMillis() - startTime;
            return true;
        }

        private String loadVideo(Task task) {
            try {
                synchronized (CacheFileManager.getInstance().getSyncObject(task.url)) {
                    if (CacheFileManager.getInstance().checkCacheFile(task.url)) {
                        return CacheFileManager.getInstance().getCacheFile(task.url).getAbsolutePath();
                    } else {
                        if (download(task)) {
                            CacheFileManager.getInstance().addCachedImage(task.url);
                            return CacheFileManager.getInstance().getCacheFile(task.url).getAbsolutePath();
                        }
                    }
                }
            } catch (Exception e) {
                task.error = "load video exception " + e.getMessage();
            }
            return null;
        }

        private String loadImage(Task task) {
            try {
                synchronized (CacheFileManager.getInstance().getSyncObject(task.url)) {
                    // 查看缓存
                    Bitmap bmp = CacheFileManager.getInstance().getCachedImage(task.url);
                    if (bmp != null) {
                        Util.safeReleaseBitmap(bmp);
                        return CacheFileManager.getInstance().getCacheFile(task.url).getAbsolutePath();
                    }
                    if (download(task)) {
                        File savedFile = CacheFileManager.getInstance().getCacheTmpFile(task.url);
                        if (Util.checkBitmap(savedFile)) {
                            CacheFileManager.getInstance().addCachedImage(task.url);
                            return CacheFileManager.getInstance().getCacheFile(task.url).getAbsolutePath();
                        } else {
                            Util.safeDeleteFile(savedFile);
                            task.error = "Decode bitmap failed";
                        }
                    }
                }
            } catch (Exception e) {
                task.error = "load image exception " + e.getMessage();
            }
            return null;
        }

        private String loadAnimRes(Task task) {
            try {
                synchronized (CacheFileManager.getInstance().getSyncObject(task.url)) {
                    // 缓存中有
                    if (CacheFileManager.getInstance().checkAnimResDir(task.url)) {
                        return CacheFileManager.getInstance().getAnimResDirPath(task.url);
                    }
                    if (download(task)) {
                        if (CacheFileManager.getInstance().addAnimResFile(task.url)) {
                            return CacheFileManager.getInstance().getAnimResDirPath(task.url);
                        } else {
                            Util.safeDeleteFile(CacheFileManager.getInstance().getCacheTmpFile(task.url));
                            task.error = "unzip anim zip failed";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                task.error = "loadAnimRes catch exception " + e.getMessage();
            }
            return null;
        }
    }
    private class ResHandler extends Handler {
        final static int MSG_TASK_FINISH = 1;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TASK_FINISH:
                    try {
                        Task task = (Task)msg.obj;
                        if (mMode == DownloadMode.MULTIPLE) {
                            mElapse = Math.max(mElapse, task.elapse);
                        } else if (mMode == DownloadMode.ONE_BY_ONE) {
                            mElapse = task.elapse;
                        }
                        if (mEventListener != null) {
                            mEventListener.onFinish(ResDownloader.this, task);
                        }
                    } catch (Exception e){
                        // NOOP
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public static class Task {
        public enum Type {
            IMAGE,
            ANIMATION,
            VIDEO,
        }
        public String url;
        public Type type;
        public int userData;
        public String error = null;
        public String data = null;
        public boolean success = false;
        public boolean complete = false;
        /**
         * 消耗的时间
         */
        public long elapse = 0;
        /**
         * 网络部分消耗的时间
         */
        public long network_elapse = 0;

        public Task(String url, Type type, int userData) {
            this.url = url;
            this.type = type;
            this.userData = userData;
        }

        public Task(String url, Type type) {
            this(url, type, 0);
        }

        @Override
        public String toString() {
            String str = "[U:";
            str += url;
            str += " ,T:";
            str += type;
            str += ",S:";
            str += success;
            str += ",EPD:";
            str += elapse;
            str += ",NEPD:";
            str += network_elapse;
            str += ",U:";
            str += userData;
            str += ",D:";
            str += data;
            str += ",E:";
            str += error;
            str += "]";
            return str;
        }
    }



    static class Util {
        /**
         * 检查文件是否为可用的bitmap
         * @param file
         * @return
         */
        static boolean checkBitmap(File file){
            if (file != null && file.exists()) {
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                    if (opts.outHeight > 0 && opts.outWidth > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    // NOOP
                }
            }
            return false;
        }


        static Bitmap safeReleaseBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                try {
                    if (!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        static void safeDeleteFile(File file) {
            if (file != null) {
                try {
                    file.delete();
                } catch (Exception e) {
                    // NOOP
                }
            }
        }
    }
}
