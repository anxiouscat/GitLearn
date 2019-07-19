package com.oversea.ads.impl;

import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdInfo implements JSONable {
    /**
     * 关联内容
     */
    String adSocialContext = "";
    /**
     * 可能的按钮上的字符串，指示按钮动作
     */
    String adCallToAction = "";
    /**
     * 标题
     */
    String adTitle = "";
    /**
     * 描述
     */
    String adBody = "";
    /**
     * Icon的Url相关数据
     */
    ImageImpl adIcon = null;
    /**
     * 详情大图的Url相关数据
     */
    ImageImpl adCoverImage = null;
    /**
     * 模板
     */
    String templateUrl = null;
    /**
     * 广告点击拉起Intent内容
     */
    String intent = "";

    /**
     * 安装成功的上报url列表
     */
    List<String> mInstalledEventUrls = new ArrayList<String>();
    /**
     * 点击广告时的上报url列表
     */
    List<String> mClickEventUrls = new ArrayList<String>();
    /**
     * 浏览时计时上报url列表
     */
    List<ShowEvent> mShowEventUrls = new ArrayList<ShowEvent>();

    public NativeAdInfo() {

    }
    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        dest.put("adSocialContext", adSocialContext);
        dest.put("adCallToAction",adCallToAction);
        dest.put("adTitle",adTitle);
        dest.put("adBody",adBody);
        dest.put("intent",intent);

        if(templateUrl != null) {
            dest.put("templateUrl",templateUrl);
        }

        JSONObject obj = new JSONObject();
        adIcon.writeToJSON(obj);
        dest.put("adIcon", obj);

        obj = new JSONObject();
        adCoverImage.writeToJSON(obj);
        dest.put("adCoverImage", obj);

        if (mClickEventUrls.size() > 0) {
            JSONArray ja = new JSONArray();
            for (String u : mClickEventUrls) {
                if (u != null && u.length() > 0) {
                    ja.put(u);
                }
            }
            dest.put("cm", ja);
        }

        if (mShowEventUrls.size() > 0) {
            JSONArray pmarray = new JSONArray();
            for (ShowEvent event : mShowEventUrls) {
                JSONObject je = new JSONObject();
                je.put("time", event.mShowTime);

                JSONArray urls = new JSONArray();
                for (String url : event.mShowPollingUrls) {
                    urls.put(url);
                }
                je.put("urls", urls);

                pmarray.put(je);
            }

            dest.put("pm", pmarray);
        }

    }

    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        this.adSocialContext = source.getString("adSocialContext");
        this.adCallToAction = source.getString("adCallToAction");
        this.adTitle = source.getString("adTitle");
        this.adBody = source.getString("adBody");
        this.intent = source.getString("intent");

        this.templateUrl = source.has("templateUrl") ? source.getString("templateUrl") : null;

        JSONObject obj = source.getJSONObject("adIcon");
        this.adIcon = ImageImpl.CREATOR.createFromJSON(obj);

        obj = source.getJSONObject("adCoverImage");
        this.adCoverImage = ImageImpl.CREATOR.createFromJSON(obj);

        mClickEventUrls.clear();
        if (source.has("cm")) {
            JSONArray array = source.getJSONArray("cm");
            if (array != null) {
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    mClickEventUrls.add(array.getString(i));
                }
            }
        }

        mInstalledEventUrls.clear();
        if (source.has("im")) {
            JSONArray array = source.getJSONArray("im");
            if (array != null) {
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    mInstalledEventUrls.add(array.getString(i));
                }
            }
        }

        mShowEventUrls.clear();
        if (source.has("pm") && !source.isNull("pm")) {

            JSONArray pmarray = source.getJSONArray("pm");
            if (pmarray != null && pmarray.length() > 0) {
                int len = pmarray.length();
                for (int i = 0; i < len; i++) {
                    JSONObject pmitem = pmarray.getJSONObject(i);
                    if (pmitem != null) {
                        if (pmitem.has("time") && pmitem.has("urls")) {
                            int time = pmitem.getInt("time");
                            JSONArray urlarray = pmitem.getJSONArray("urls");

                            if (time >= 0 && urlarray != null && urlarray.length() > 0) {
                                ShowEvent event = new ShowEvent();
                                event.mShowTime = time;

                                int urllen = urlarray.length();
                                for (int j = 0; j < urllen; j++) {
                                    String url = urlarray.getString(j);
                                    if (url != null && url.length() > 0) {
                                        event.mShowPollingUrls.add(url);
                                    }
                                }

                                //插入并排序
                                if (event.mShowPollingUrls.size() > 0) {
                                    int size = mShowEventUrls.size();
                                    boolean inserted = false;
                                    for (int k = 0; k < size; k++) {
                                        ShowEvent e = mShowEventUrls.get(k);
                                        if (e.mShowTime > time) {
                                            mShowEventUrls.add(k, event);
                                            inserted = true;
                                            break;
                                        }
                                    }
                                    if (!inserted) {
                                        mShowEventUrls.add(event);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    public static JSONCreator<NativeAdInfo> CREATOR = new JSONCreator<NativeAdInfo>(NativeAdInfo.class);

    public List<String> getInstalledEventUrls() {
        return mInstalledEventUrls;
    }

    public int getNextShowCheckTime(int prvtime) {
        if (mShowEventUrls.size() == 0) {
            return -1;
        }

        for (ShowEvent e : mShowEventUrls) {
            if (e.mShowTime > prvtime) {
                return e.mShowTime;
            }
        }

        return -1;
    }

    public ArrayList<String> getShowPollingUrls(int showtime) {
        if (showtime < 0 || mShowEventUrls.size() == 0) {
            return null;
        }

        for (ShowEvent e : mShowEventUrls) {
            if (e.mShowTime == showtime) {
                return e.mShowPollingUrls;
            }
        }

        return null;
    }

    private class ShowEvent {
        public int mShowTime = 0;
        public ArrayList<String> mShowPollingUrls = new ArrayList<String>();
    }



    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            writeToJSON(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
