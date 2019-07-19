package com.oversea.ads.data;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.io.BaseJSONable;
import com.oversea.ads.io.JSONCreator;

public class BaseRequest extends BaseJSONable {
	public String version = Cfg.VERSION;
	public boolean isWifi = false;
	public DeviceInfo deviceInfo = DeviceInfo.getDeviceInfo();
	public String country;
	public String language;
	public long clientID = 0;
	public String ua = null;

	public BaseRequest() {
		ua = getCurrentUserAgent();
	}
	public BaseRequest(boolean isWifi,long clientID,String country,String language) {
		this.isWifi = isWifi;
		this.clientID = clientID;
		this.country = country;
		this.language = language;
		this.ua = getCurrentUserAgent();
	}
	@Override
	public void writeToJSON(JSONObject dest) throws JSONException {
		dest.put("version", version);
		dest.put("isWifi", isWifi);
		dest.put("clientID", clientID);
		dest.put("country", country);
		dest.put("language", language);

		JSONObject obj = new JSONObject();
		deviceInfo.writeToJSON(obj);
		dest.put("deviceInfo", obj);

		dest.put("ua", ua);
	}
	@Override
	public void readFromJSON(JSONObject source) throws JSONException {
		version = source.getString("version");
		isWifi = source.getBoolean("isWifi");
		clientID = source.getLong("clientID");
		country = source.getString("country");
		language = source.getString("language");
		
		JSONObject obj = source.getJSONObject("deviceInfo");
		deviceInfo = DeviceInfo.CREATOR.createFromJSON(obj);

		if (source.has("ua")) {
			ua = source.getString("ua");
		}
	}
	public static JSONCreator<BaseRequest> CREATOR = new JSONCreator<BaseRequest>(BaseRequest.class);

	private String getCurrentUserAgent() {
		StringBuffer buffer = new StringBuffer();
		// Add version
		final String version = Build.VERSION.RELEASE;
		if (version.length() > 0) {
			buffer.append(version);
		} else {
			// default to "1.0"
			buffer.append("1.0");
		}
		buffer.append("; ");

		if (language != null) {
			buffer.append(language.toLowerCase());

			if (country != null) {
				buffer.append("-");
				buffer.append(country.toLowerCase());
			}
		} else {
			// default to "en"
			buffer.append("en");
		}
		// add the model for the release build
		if ("REL".equals(Build.VERSION.CODENAME)) {
			final String model = Build.MODEL;
			if (model.length() > 0) {
				buffer.append("; ");
				buffer.append(model);
			}
		}
		final String id = Build.ID;
		if (id.length() > 0) {
			buffer.append(" Build/");
			buffer.append(id);
		}

		return "Mozilla/5.0 (Linux; U; Android "
				+ buffer.toString()
				+ ") AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	}
}
