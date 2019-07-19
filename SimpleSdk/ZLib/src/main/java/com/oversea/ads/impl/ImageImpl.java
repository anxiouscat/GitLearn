package com.oversea.ads.impl;

import com.oversea.ads.base.ImageBase;
import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class ImageImpl implements ImageBase,JSONable{
    String url = "";
    int width = 0;
    int height = 0;
    public ImageImpl() {

    }
    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        dest.put("url",url);
        dest.put("width",width);
        dest.put("height",height);
    }

    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        url = source.getString("url");
        width = source.getInt("width");
        height = source.getInt("height");
    }
    public static JSONCreator<ImageImpl> CREATOR = new JSONCreator<ImageImpl>(ImageImpl.class);
}
