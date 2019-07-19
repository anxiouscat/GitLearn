package com.oversea.ads.io;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayCreator<T extends JSONArrayable> implements JSONArrayable.Creator<T> {
	public Class<T> mClassT;
	public JSONArrayCreator(Class<T> classT) {
		mClassT = classT;
	}
	@Override
	public T createFromJSON(JSONArray source) throws JSONException {
		T t;
		try {
			t = mClassT.newInstance();
		} catch (Exception e) {
			throw new JSONException("newInstance failed!");
		} 
		t.readFromJSON(source);
		return t;
	}
}
