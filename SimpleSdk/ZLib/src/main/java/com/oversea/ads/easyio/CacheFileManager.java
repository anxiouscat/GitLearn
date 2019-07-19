package com.oversea.ads.easyio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.oversea.ads.AdsSDK;
import com.oversea.ads.util.LogEx;
import com.oversea.ads.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * 缓存管理
 */
public class CacheFileManager {
	private static final String TAG = "CacheFileManager";
	private static final String SUCCESS_FILE_END_PRIPY = ".suces";
	private static final String CACHE_DIR = "main_img_cache";
	private static final String CACHE_TEMP_DIR = "tmp";
	private static final String ANIM_RES_DIR = "anim_res";
	private static final String JSON_RES_DIR = "json_res";
	private static final String ANIM_RES_SUCCESS_FILE = "success";
	private static final long MAX_CACHE_TIME = 1000 * 60 * 60 * 4;
	
	private static CacheFileManager sInstance;
	
	private Context mContext;
	private String mCacheDir;
	private String mCacheTempDir;
	private String mAnimResDir;
	private String mJsonResDir;

	public WorkHandler mWorkHandler;
	private HandlerThread mThread;
	
	private HashMap<String, Object> mSyncObjMap = new HashMap<String, Object>();
	
	private CacheFileManager() {
	}


	public static void writeJson(String placemenId, String json) {
		File file = CacheFileManager.getInstance().getJsonFile(placemenId);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, false);
			writer.write(json);
			LogEx.getInstance().d(TAG,
					" write json for placemenId: " + placemenId);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			Util.safeClose(writer);
		}
	}

	public static boolean hasJsonCache(String placemenId) {
		return CacheFileManager.getInstance().checkJsonFile(placemenId);
	}

	public static String readJson(String placemenId) {
		if (CacheFileManager.getInstance().checkJsonFile(placemenId)) {
			File file = CacheFileManager.getInstance().getJsonFile(placemenId);
			FileInputStream fis = null;
			ByteArrayOutputStream bos = null;
			try {
				fis = new FileInputStream(file);
				bos = new ByteArrayOutputStream();

				byte[] buff = new byte[1024];
				int len;
				while ((len = fis.read(buff)) > 0) {
					bos.write(buff, 0, len);
				}
				String content = new String(bos.toByteArray());
				LogEx.getInstance().d(TAG,
						" read json for placemenId: " + placemenId);
				return content;
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				Util.safeClose(fis);
				Util.safeClose(bos);
			}
		}
		return null;
	}


	
	public synchronized static CacheFileManager getInstance() {
		if (sInstance == null) {
			sInstance = new CacheFileManager();
		}
		
		return sInstance;
	}
	
	public synchronized void setContext(Context appContext, Looper looper) {
		if (mContext != null || appContext == null) {
			return;
		}
		
		mContext = appContext;
		File cachedir = null;
		try {
			cachedir = mContext.getDir(CACHE_DIR, 0);
		} catch (Exception e) {
		}
		mCacheDir = cachedir != null ? cachedir.getAbsolutePath() : null;
		
		if (mCacheDir != null) {
			try {
				mCacheTempDir = mCacheDir + File.separator + CACHE_TEMP_DIR;
				File tmpdir = new File(mCacheTempDir);
				if (tmpdir.exists()) {
					FileUtil.deleteDirSubFile(tmpdir);
				} else {
					tmpdir.mkdir();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			File animdir = mContext.getDir(ANIM_RES_DIR, 0);
			mAnimResDir = animdir.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File dir = mContext.getDir(JSON_RES_DIR, 0);
			mJsonResDir = dir.getAbsolutePath();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if (looper == null || looper == Looper.getMainLooper()) {
			mThread = new HandlerThread("cachescanthread");
			mThread.start();
			looper = mThread.getLooper();
		}
		mWorkHandler = new WorkHandler(looper);
		mWorkHandler.sendEmptyMessage(WorkHandler.MSG_SCAN_CACHE_DIR);
	}
	
	public synchronized void destroy() {
		try {
			if (mContext != null) {
				if (mThread != null) {
					mThread.quit();
					mThread = null;
				}
				
				mWorkHandler.destroy();
				mWorkHandler = null;
				
				mContext = null;
				mCacheDir = null;
				mCacheTempDir = null;
				mJsonResDir = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized File getJsonFile(String placementId) {
		try {
			if (mContext != null && placementId != null && placementId.length() > 0) {
				String path = mJsonResDir + File.separator + placementId + ".json";
				return new File(path);
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "getJsonFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		LogEx.getInstance().e(TAG, "getJsonFile() return null! placementId=" + placementId);
		return null;
	}

	public synchronized boolean checkJsonFile(String placementId) {
		try {
			if (mContext != null && placementId != null && placementId.length() > 0) {
				String path = mJsonResDir + File.separator + placementId + ".json";
				File saveFile = new File(path);
				return saveFile.exists() && saveFile.isFile();
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "checkJsonFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized File getCacheFile(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				String savefilepath = mCacheDir + File.separator + url.hashCode();
				return new File(savefilepath);
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "getCacheFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		LogEx.getInstance().e(TAG, "getCacheFile() return null! url=" + url);
		return null;
	}
	
	public synchronized File getCacheTmpFile(String url) {
		try {
			if (mCacheDir != null && url != null && url.length() > 0) {
				String savefilepath = mCacheTempDir + File.separator + url.hashCode();
				return new File(savefilepath);
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "getCacheTmpFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		LogEx.getInstance().e(TAG, "getCacheTmpFile() return null! url=" + url);
		return null;
	}
	
	public synchronized boolean checkCacheFile(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				String savefilepath = mCacheDir + File.separator + url.hashCode();
				File savefile = new File(savefilepath);
				File succussfile = new File(savefilepath + SUCCESS_FILE_END_PRIPY);
				
				if (succussfile.exists() && savefile.exists() && savefile.isFile()) {
					return true;
				}
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "checkCacheFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized Bitmap getCachedImage(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				String savefilepath = mCacheDir + File.separator + url.hashCode();
				File savefile = new File(savefilepath);
				File successfile = new File(savefilepath + SUCCESS_FILE_END_PRIPY);
				
				if (savefile.exists() && successfile.exists()) {
					Bitmap bmp = BitmapFactory.decodeFile(savefilepath);
					if (bmp != null) {
						return bmp;
					} 
				}
				
				successfile.delete();
				savefile.delete();
				return null;
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "getCachedImage() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public synchronized boolean addCachedImage(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				String savefilepath = mCacheDir + File.separator + url.hashCode();
				File savefile = new File(savefilepath);
				if (!savefile.exists()) {
					File tmpfile = getCacheTmpFile(url);
					if (tmpfile != null && tmpfile.exists()) {
						if (!tmpfile.renameTo(savefile)) {
							return false;
						}
					} else {
						return false;
					}
				}
				
				File successfile = new File(savefilepath + SUCCESS_FILE_END_PRIPY);
				if (!successfile.exists()) {
					try {
						return successfile.createNewFile();
					} catch (Exception e) {
						LogEx.getInstance().e(TAG, "addCachedImage() createNewFile catch " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "addCachedImage() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized boolean checkAnimResDir(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				String savefilepath = mAnimResDir + File.separator + url.hashCode();
				File savefile = new File(savefilepath);
				
				if (savefile.exists() && savefile.isDirectory()) {
					String sucpath = savefilepath + File.separator + ANIM_RES_SUCCESS_FILE;
					File sucfile = new File(sucpath);
					if (sucfile.exists()) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "checkAnimResDir() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized String getAnimResDirPath(String url) {
		if (mContext != null && url != null && url.length() > 0) {
			String savefilepath = mAnimResDir + File.separator + url.hashCode();
			return savefilepath;
		}
		
		return null;
	}
	
	public synchronized boolean addAnimResFile(String url) {
		try {
			if (mContext != null && url != null && url.length() > 0) {
				File tmpfile = getCacheTmpFile(url);
				if (tmpfile != null && tmpfile.exists()) {
					String savefilepath = mAnimResDir + File.separator + url.hashCode();
					File savefile = new File(savefilepath);
					
					String sucpath = savefilepath + File.separator + ANIM_RES_SUCCESS_FILE;
					File sucfile = new File(sucpath);
					
					if (savefile.exists() && sucfile.exists()) {
						return true;
					} else {
						FileUtil.deleteFile(savefile);
					}
					savefile.mkdir();
					
					if (FileUtil.unzipFile(tmpfile, savefile)) {
						FileUtil.createNewFile(sucfile);
						return true;
					} else {
						tmpfile.delete();
						FileUtil.deleteFile(savefile);
					}
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			LogEx.getInstance().e(TAG, "addAnimResFile() catch " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized Object getSyncObject(String url) {
		Object syncobj = mSyncObjMap.get(url);
		if (syncobj == null) {
			syncobj = new Object();
			
			mSyncObjMap.put(url, syncobj);
		}
		
		return syncobj;
	}
	
	public class WorkHandler extends Handler {
		
		private static final int MSG_SCAN_CACHE_DIR = 0;
		
		public WorkHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_SCAN_CACHE_DIR:
				scanCacheDir();
				removeMessages(MSG_SCAN_CACHE_DIR);
				sendEmptyMessageDelayed(MSG_SCAN_CACHE_DIR, MAX_CACHE_TIME);
				break;
			}
		}
		
		public void destroy() {
			removeMessages(MSG_SCAN_CACHE_DIR);
		}
	}
	
	private synchronized void scanCacheDir() {
		LogEx.getInstance().i(TAG, "scanCacheDir() start");
		try {
			if (mContext != null) {
				releseCacheDir(mCacheDir, false);
				releseCacheDir(mAnimResDir, false);
				releseCacheDir(mJsonResDir, !AdsSDK.isCacheMode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void releseCacheDir(String cacheDir, boolean forceDelete) {
		if (cacheDir != null) {
			File cachedir = new File(cacheDir);
			File[] filelist = cachedir.listFiles();

			if (filelist != null && filelist.length > 0) {
				long currtime = System.currentTimeMillis();
				for (int i = 0; i < filelist.length; i++) {
					try {
						File f = filelist[i];
						if (!f.exists() || !f.isFile()) {
							continue;
						}

						String path = f.getAbsolutePath();
						String silepath = path + SUCCESS_FILE_END_PRIPY;
						File sfile = new File(silepath);
						long creatime = f.lastModified();
						boolean isOutTime = currtime - creatime > MAX_CACHE_TIME;
						if(isOutTime || forceDelete) {
							FileUtil.deleteFile(sfile);
							FileUtil.deleteFile(f);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}


