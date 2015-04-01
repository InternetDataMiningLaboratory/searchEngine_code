package com.proj.utils;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonUtils {
	public static String toJson(Object object) {
		// Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
		// .create();

		GsonBuilder builder = new GsonBuilder();
		// builder.excludeFieldsWithoutExposeAnnotation();//不转换没有@Expose注解的字段
		// builder.setExclusionStrategies(new ExclusionStrategy());
		// builder.enableComplexMapKeySerialization();
		builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
		builder.registerTypeAdapter(Timestamp.class,
				new JsonSerializer<Timestamp>() {

					@Override
					public JsonElement serialize(Timestamp date, Type arg1,
							JsonSerializationContext arg2) {
						return new JsonPrimitive(DateCalUtil.diffNow(date));
					}

				});
		Gson gson = builder.create();

		String json = gson.toJson(object);
		return json;
	}
}