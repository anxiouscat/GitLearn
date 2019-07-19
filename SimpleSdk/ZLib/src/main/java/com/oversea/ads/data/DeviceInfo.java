package com.oversea.ads.data;

import com.oversea.ads.io.BaseJSONable;
import com.oversea.ads.io.JSONCreator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class DeviceInfo extends BaseJSONable{
    public static DeviceInfo mDeviceInfo = new DeviceInfo();
    public String imei = "";
    public String androidId = "";
    public int androidVersion;
    public String phoneModel = "";
    public int mScreenWidth = 0;
    public int mScreenHeight = 0;
    public DeviceInfo() {
    }
    public DeviceInfo(String imei,String androidId,int androidVersion,String phoneModel) {
        setValue(imei,androidId,androidVersion,phoneModel, 0, 0);
    }
    public DeviceInfo(DeviceInfo info) {
        setValue(info.imei,info.androidId,info.androidVersion,info.phoneModel, info.mScreenWidth, info.mScreenHeight);
    }
    public static DeviceInfo getStaticDeviceInfo() {
        return mDeviceInfo;
    }
    public static DeviceInfo getDeviceInfo() {
        return new DeviceInfo(mDeviceInfo);
    }
    public static void setDeviceInfo(String imei,String androidId,int androidVersion,String phoneModel, int screenW, int screenH) {
        mDeviceInfo.setValue(imei,androidId,androidVersion,phoneModel, screenW, screenH);
    }
    public void setValue(String imei,String androidId,int androidVersion,String phoneModel, int screenW, int screenH) {
        this.imei = imei;
        this.androidId = androidId;
        this.androidVersion = androidVersion;
        this.phoneModel = phoneModel;
        this.mScreenWidth = screenW;
        this.mScreenHeight = screenH;
    }
    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        dest.put("imei", this.imei);
        dest.put("androidId", this.androidId);
        dest.put("androidVersion", this.androidVersion);
        dest.put("phoneModel", this.phoneModel);
        dest.put("screenw", this.mScreenWidth);
        dest.put("screenh", this.mScreenHeight);
    }
    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        this.imei = source.getString("imei");
        this.androidId = source.getString("androidId");
        this.androidVersion = source.getInt("androidVersion");
        this.phoneModel = source.getString("phoneModel");
        this.mScreenWidth = source.getInt("screenw");
        this.mScreenHeight = source.getInt("screenh");
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if( !(o instanceof DeviceInfo) ) {
            return false;
        }
        DeviceInfo dst = (DeviceInfo)o;
        if(!this.imei.equals(dst.imei)) {
            return false;
        }
        if(!this.androidId.equals(dst.androidId)) {
            return false;
        }
        if(this.androidVersion != dst.androidVersion) {
            return false;
        }
        if(!this.phoneModel.equals(dst.phoneModel)) {
            return false;
        }
        return true;
    }

    public static JSONCreator<DeviceInfo> CREATOR = new JSONCreator<DeviceInfo>(DeviceInfo.class);
}
