package com.oversea.ads.impl;

import com.oversea.ads.data.BaseRequest;
import com.oversea.ads.io.JSONCreator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a_zcg_000 on 2016/8/4.
 */
public class NativeAdRequest extends BaseRequest {
	String placementId;
	String channel;
	int count;
	int width = -1;
	int height = -1;

	public NativeAdRequest() {
		super();
	}

	public NativeAdRequest(boolean isWifi, long clientID, String country,
			String language, String placementId, String channel, int count) {
		this(isWifi, clientID, country, language, placementId, channel, count,
				-1, -1);
	}

	public NativeAdRequest(boolean isWifi, long clientID, String country,
			String language, String placementId, String channel, int count,
			int width, int height) {
		super(isWifi, clientID, country, language);
		this.placementId = placementId;
		this.channel = channel;
		this.count = count;
		this.width = width;
		this.height = height;
	}

	@Override
	public void writeToJSON(JSONObject dest) throws JSONException {
		super.writeToJSON(dest);
		dest.put("placementId", placementId);
		dest.put("channel", channel != null ? channel : "");
		dest.put("count", count);
		dest.put("width", width);
		dest.put("height", height);
	}

	@Override
	public void readFromJSON(JSONObject source) throws JSONException {
		super.readFromJSON(source);
		placementId = source.getString("placementId");
		count = source.getInt("count");
		if (source.has("width")) {
			width = source.getInt("width");
		} else {
			width = -1;
		}
		if (source.has("height")) {
			height = source.getInt("height");
		} else {
			height = -1;
		}

		if (source.has("channel")) {
			this.channel = source.getString("channel");
		}
	}

	public static JSONCreator<NativeAdRequest> CREATOR = new JSONCreator<NativeAdRequest>(
			NativeAdRequest.class);
}
