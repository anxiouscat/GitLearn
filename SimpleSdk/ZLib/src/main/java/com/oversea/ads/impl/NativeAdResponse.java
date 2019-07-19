package com.oversea.ads.impl;

import com.oversea.ads.data.BaseRequest;
import com.oversea.ads.data.BaseResponse;
import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdResponse extends BaseResponse {
    public int count;
    public ArrayList<NativeAdInfo> nativeAdInfos = new ArrayList<NativeAdInfo>();
    public NativeAdResponse() {
        super();
    }
    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        dest.put("count",count);
        JSONArray jsonArray = new JSONArray();
        for(NativeAdInfo info:nativeAdInfos) {
            JSONObject obj = new JSONObject();
            info.writeToJSON(obj);
            jsonArray.put(obj);
        }
        dest.put("nativeAdInfos", jsonArray);
        super.writeToJSON(dest);
    }

    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        count = source.has("count") ? source.getInt("count") : 0;
        nativeAdInfos.clear();
        JSONArray jsonArray = source.getJSONArray("nativeAdInfos");
        for(int i = 0 ;i < jsonArray.length();i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            NativeAdInfo action = NativeAdInfo.CREATOR.createFromJSON(obj);
            nativeAdInfos.add(action);
        }
        super.readFromJSON(source);
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

    public static JSONCreator<NativeAdResponse> CREATOR = new JSONCreator<NativeAdResponse>(NativeAdResponse.class);
}
