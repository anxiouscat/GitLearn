package com.oversea.ads.impl;

import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.io.JSONCreator;
import com.oversea.ads.io.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class AdErrorImpl implements AdErrorBase,JSONable{
    public static final int NETWORK_ERROR_CODE = 1000;
    public static final int NO_FILL_ERROR_CODE = 1001;
    public static final int LOAD_TOO_FREQUENTLY_ERROR_CODE = 1002;
    public static final int SERVER_ERROR_CODE = 2000;
    public static final int INTERNAL_ERROR_CODE = 2001;
    public static final int MEDIATION_ERROR_CODE = 3001;
    public static final AdErrorImpl NETWORK_ERROR = new AdErrorImpl(1000, "Network Error");
    public static final AdErrorImpl NO_FILL = new AdErrorImpl(1001, "No Fill");
    public static final AdErrorImpl LOAD_TOO_FREQUENTLY = new AdErrorImpl(1002, "Ad was re-loaded too frequently");
    public static final AdErrorImpl SERVER_ERROR = new AdErrorImpl(2000, "Server Error");
    public static final AdErrorImpl INTERNAL_ERROR = new AdErrorImpl(2001, "Internal Error");
    public static final AdErrorImpl MEDIATION_ERROR = new AdErrorImpl(3001, "Mediation Error");

    int errorCode;
    String errorMessage;
    public AdErrorImpl() {
    }
    public AdErrorImpl(int errorCode,String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void writeToJSON(JSONObject dest) throws JSONException {
        dest.put("errorCode",errorCode);
        dest.put("errorMessage",errorMessage);
    }

    @Override
    public void readFromJSON(JSONObject source) throws JSONException {
        errorCode = source.getInt("errorCode");
        errorMessage = source.getString("errorMessage");
    }
    public static JSONCreator<AdErrorImpl> CREATOR = new JSONCreator<AdErrorImpl>(AdErrorImpl.class);
}
