package com.zhiri.bear.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonParser {

	public static Gson gson = new Gson();

	@SuppressWarnings("hiding")
	public static <T> T deserializeByJson(String data, Type type) {
		if (TextUtil.isValidate(data)) {
			return gson.fromJson(data, type);
		}
		return null;
	}

	@SuppressWarnings("hiding")
	public static <T> T deserializeByJson(String data, Class<T> clz) {
		if (TextUtil.isValidate(data)) {
			return gson.fromJson(data, clz);
		}
		return null;
	}

	@SuppressWarnings("hiding")
	public static <T> String serializeToJson(T t) {
		if (t == null) {
			return "";
		}
		return gson.toJson(t);
	}

	@SuppressWarnings("hiding")
	public static <T> String serializeToJsonForGsonBuilder(T t) {
		if (t == null) {
			return "";
		}
		Gson gs = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gs.toJson(t);
	}

}
