package com.yj.webtool.highperf;

import com.google.gson.Gson;

public class GsonUtils {
	static Gson gson = new Gson();
	public static Object fromJson(String json, Class classT) {
		return gson.fromJson(json, classT);
	}
	
	public static String toString(Object obj) {
		return gson.toJson(obj);
	}
}
