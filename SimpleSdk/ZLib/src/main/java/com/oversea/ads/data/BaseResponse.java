package com.oversea.ads.data;

import com.oversea.ads.configmanagr.ConfigInfos;
import com.oversea.ads.io.BaseJSONable;
import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class BaseResponse extends BaseJSONable {
    public ConfigInfos configInfos;
    public BaseResponse() {
        this(new ConfigInfos());
    }
    public BaseResponse(ConfigInfos configInfos) {
        this.configInfos = configInfos;
    }
    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        JSONObject obj = new JSONObject();
        configInfos.writeToJSON(obj);
        dest.put("configInfos", obj);
    }
    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        JSONObject obj = source.getJSONObject("configInfos");
        configInfos.readFromJSON(obj);
    }
    public static JSONCreator<BaseResponse> CREATOR = new JSONCreator<BaseResponse>(BaseResponse.class);
}
