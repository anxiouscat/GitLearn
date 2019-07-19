package com.oversea.ads.configmanagr;

import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class ConfigInfos implements JSONable{
    public HashMap<String,String> configInfos = new HashMap<String,String>();
    public ConfigInfos() {
    }
    public ConfigInfos(HashMap<String,String> values) {
        configInfos.putAll(values);
    }
    public HashMap<String,String> getMap() {
        return configInfos;
    }
    public void put(String key,String value) {
        this.configInfos.put(key,value);
    }
    public String get(String key) {
        return this.configInfos.get(key);
    }
    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for(String key:configInfos.keySet()) {
            JSONObject obj = new JSONObject();
            obj.put("key",key);
            obj.put("value",this.configInfos.get(key));
            jsonArray.put(obj);
        }
        dest.put("configInfos", jsonArray);
    }
    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        configInfos.clear();
        JSONArray jsonArray = source.getJSONArray("configInfos");
        for(int i = 0 ;i < jsonArray.length();i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String key = obj.getString("key");
            String value = obj.getString("value");
            configInfos.put(key,value);
        }
    }
    public static JSONCreator<ConfigInfos> CREATOR = new JSONCreator<ConfigInfos>(ConfigInfos.class);
}
