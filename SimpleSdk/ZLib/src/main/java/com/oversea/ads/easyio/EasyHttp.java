package com.oversea.ads.easyio;

import com.oversea.ads.io.JSONArrayable;
import com.oversea.ads.io.JSONable;
import com.oversea.ads.util.DesEncrypt;
import com.oversea.ads.util.LogEx;
import com.oversea.ads.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class EasyHttp {
    private static final String TAG = "EasyHttp";

    public static <T extends JSONArrayable> T post(String url, InputStream[] data, JSONArrayable.Creator<T> creator) throws Exception {
        JSONArray receiveJSON = null;
        ByteArrayInputStream request = null;
        ByteArrayOutputStream response = null;
        T t = null;
        try {
            response = new ByteArrayOutputStream(1024 * 10);
            post(url, data, response);
            receiveJSON = new JSONArray(new String(response.toByteArray(), "utf-8"));
            t = creator.createFromJSON(receiveJSON);
        } catch (Exception e) {
            throw e;
        } finally {
            if (request != null) {
                request.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return t;
    }

    public static <T extends JSONable> T post(String url, InputStream[] data, JSONable.Creator<T> creator) throws Exception {
        JSONObject receiveJSON = null;
        ByteArrayInputStream request = null;
        ByteArrayOutputStream response = null;
        T t = null;
        try {
            response = new ByteArrayOutputStream(1024 * 10);
            post(url, data, response);
            receiveJSON = new JSONObject(new String(response.toByteArray(), "utf-8"));
            t = creator.createFromJSON(receiveJSON);
        } catch (Exception e) {
            throw e;
        } finally {
            if (request != null) {
                request.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return t;
    }

    public static <T extends JSONable> T post(String url, JSONable data, JSONable.Creator<T> creator) throws Exception {
        JSONObject receiveJSON = null;
        JSONObject sendJSON = new JSONObject();
        ByteArrayInputStream request = null;
        ByteArrayOutputStream response = null;
        T t = null;
        try {
            data.writeToJSON(sendJSON);
            String str = doEncrypt(sendJSON.toString());

            request = new ByteArrayInputStream(str.getBytes("utf-8"));
            response = new ByteArrayOutputStream(1024 * 10);
            post(url, new InputStream[]{request}, response);
            String receiveStr = new String(response.toByteArray(), "utf-8");
            LogEx.getInstance().d(TAG, "receiveStr == " + receiveStr);
            receiveJSON = new JSONObject(receiveStr);

            receiveJSON = decode(receiveJSON);

            t = creator.createFromJSON(receiveJSON);
        } catch (Exception e) {
            throw e;
        } finally {
            if (request != null) {
                request.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return t;
    }

    public static void post(String url, InputStream[] request, OutputStream response) throws Exception {
        LogEx.getInstance().d(TAG, "post - ");
        LogEx.getInstance().d(TAG, "url == " + url);
        URL uri;
        uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.addRequestProperty("content-type", "application/vnd.syncml+xml; charset=UTF-8");

        try {
            onSend(request, conn);

            onRecieve(response, conn);
        } finally {
            try {
                conn.getInputStream().close();
            } finally {
                conn.disconnect();
            }
        }

        LogEx.getInstance().d(TAG, "post + ");
    }

    static void onSend(InputStream[] request, HttpURLConnection conn) throws IOException {
        OutputStream os = null;
        //如果有数据需要发送,开始发送数据//
        if (request != null) {
            try {
                os = conn.getOutputStream();
                LogEx.getInstance().d(TAG, "getOutputStream");
                for (InputStream is : request) {
                    byte[] buffer = new byte[1024 * 10];
                    while (true) {
                        int length = is.read(buffer);
                        LogEx.getInstance().d(TAG, "length == " + length);
                        if (length == -1) {
                            break;
                        }
                        os.write(buffer, 0, length);
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (os != null) {
                    os.flush();
                    //os.close();
                }
            }
        }
    }

    static void onRecieve(OutputStream response, HttpURLConnection conn) throws Exception {
        //准备接收回应//
        int res = conn.getResponseCode();
        LogEx.getInstance().d(TAG, "res == " + res);
        if (res != 200) {
            throw new Exception("responseCode == " + res);
        }
        if (response != null) {
            InputStream is = null;
            try {
                is = conn.getInputStream();
                LogEx.getInstance().d(TAG, "getInputStream");
                byte[] buffer = new byte[1024 * 10];
                while (true) {
                    int length = is.read(buffer);
                    LogEx.getInstance().d(TAG, "length == " + length);
                    if (length <= 0) {
                        break;
                    }
                    response.write(buffer, 0, length);
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (is != null) {
                    is.close();
                    is = null;
                }
            }
        }
    }

    public static void get(String url, String param, OutputStream response) throws Exception {
        LogEx.getInstance().d(TAG, "get - ");
        LogEx.getInstance().d(TAG, "url == " + url);
        URL uri;
        HttpURLConnection conn = null;
        try {
            uri = new URL(url + (param == null ? "" : ("?" + param)));
            conn = (HttpURLConnection) uri.openConnection();
            conn.connect();

            //准备接收回应//
            int res = conn.getResponseCode();
            if (res != HttpURLConnection.HTTP_OK) {
                throw new Exception("responseCode == " + res);
            }
            if (response != null) {
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 10];
                    while (true) {
                        int length = is.read(buffer);
                        if (length <= 0) {
                            break;
                        }
                        response.write(buffer, 0, length);
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                conn.getInputStream().close();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        LogEx.getInstance().d(TAG, "get + ");
    }

    static JSONObject decode(JSONObject json) throws Exception {
        if (json.has("encrpever") && json.has("timestamp") && json.has("data")) {
            int encrpever = json.getInt("encrpever");
            long key = json.getLong("timestamp");
            String data = json.getString("data");

            String decryptdata = DesEncrypt.decryptString(data, String.valueOf(key));
            return new JSONObject(decryptdata);
        }
        return json;
    }

    static String doEncrypt(String data) throws Exception {
        long mTimeStamp = System.currentTimeMillis();
        String mData = DesEncrypt.encrypt(data, String.valueOf(mTimeStamp));
        JSONObject json = new JSONObject();
        json.put("encrpever", 1);
        json.put("timestamp", mTimeStamp);
        json.put("data", mData);
        return json.toString();
    }


    public static final boolean downloadFile(String url, Map<String, String> propertyMap, File savefile) {
        return downloadFile(url, propertyMap, savefile, null);
    }

    public static final boolean downloadFile(String url, Map<String, String> propertyMap, File saveFile, DownloadCallback downloadCallback) {
        if (url == null || url.length() == 0) {
            if (downloadCallback != null) {
                downloadCallback.onDownloadFailed("download url is null");
            }
            return false;
        }

        RandomAccessFile fos = null;
        long fileLength = 0;
        if (saveFile != null) {
            try {
                if (!saveFile.exists()) {
                    FileUtil.createNewFile(saveFile);
                } else {
                    fileLength = saveFile.length();
                }
                fos = new RandomAccessFile(saveFile, "rw");
            } catch (Exception e1) {
                if (downloadCallback != null) {
                    downloadCallback.onDownloadFailed(e1.getMessage());
                }
                return false;
            }
        }

        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) getRealURL(url).openConnection();
            conn.setRequestProperty("User-Agent", Util.getUserAgent());
            try {
                if (propertyMap != null) {
                    Set<String> keyset = propertyMap.keySet();
                    if (keyset != null) {
                        for (String key : keyset) {
                            String value = propertyMap.get(key);
                            if (key != null && key.length() > 0 && value != null && value.length() > 0) {
                                conn.setRequestProperty(key, value);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            if (fileLength > 0 && fos != null) {
                conn.setRequestProperty("Range", "bytes=" + fileLength + "-");
                fos.seek(fileLength);
            }

            conn.connect();

            int length = conn.getContentLength();
            /**
             * 这里是因为有些服务器没有返回文件大小，对于这样的是没法断点续传的
             */
            if (length == 0 && fileLength > 0) {
                if (saveFile != null) {
                    saveFile.delete();
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                    }
                    if (conn != null) {
                        conn.disconnect();
                        conn = null;
                    }

                    return downloadFile(url, propertyMap, saveFile, downloadCallback);
                }
            }

            if (length == fileLength) {
                if (downloadCallback != null) {
                    downloadCallback.onDownloadComplete();
                }
                return true;
            }

            is = conn.getInputStream();

            int savecnt = 0;
            final int defaultBufsize = 1024 * 8;
            byte[] buffer = new byte[defaultBufsize];
            while (true) {
                if (downloadCallback != null && (downloadCallback.isStop() || downloadCallback.isCancel())) {
                    if (downloadCallback.isStop()) {
                        downloadCallback.stop();
                    }
                    if (downloadCallback.isCancel()) {
                        downloadCallback.cancel();
                    }

                    return false;
                }
                int count = is.read(buffer);
                if (count < 0) {
                    break;
                }
                if (count == 0) {
                    Thread.sleep(1);
                    continue;
                }

                if (fos != null) {
                    fos.write(buffer, 0, count);
                }
                savecnt += count;
                if (downloadCallback != null) {
                    downloadCallback.onDownloading(length + fileLength,
                            savecnt + fileLength);
                }
            }

            if (length > 0 && savecnt != length) {
                if (downloadCallback != null) {
                    downloadCallback.onDownloadFailed("downLoad length is not finished");
                }
                return false;
            }

            if (downloadCallback != null) {
                downloadCallback.onDownloadComplete();
            }
            return true;
        } catch (Exception e) {
            if (downloadCallback != null) {
                downloadCallback.onDownloadFailed(e.getMessage());
            }
            return false;
        } finally {
            safeClose(is);
            safeClose(fos);

            if (conn != null) {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
        }
    }

    static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final URL getRealURL(String url) throws Exception {
        URL curUrl = new URL(url);
        HttpURLConnection conn = null;
        int jumpCount = 0;
        while (jumpCount < 8) {
            conn = (HttpURLConnection) curUrl.openConnection();
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM || code == 307) {
                curUrl = new URL(conn.getHeaderField("Location"));
                try {
                    conn.getInputStream().close();
                } finally {
                    conn.disconnect();
                }
                jumpCount++;
                continue;
            }
            try {
                conn.getInputStream().close();
            } finally {
                conn.disconnect();
            }
            return curUrl;
        }

        return new URL(url);
    }

    public interface DownloadCallback {
        void onDownloadFailed(String errorMsg);

        void onDownloading(long totalLength, long downloadedLength);

        void onDownloadComplete();

        boolean isStop();

        void stop();

        boolean isCancel();

        void cancel();
    }

}
