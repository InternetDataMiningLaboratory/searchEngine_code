package com.proj.utils;

import java.util.UUID;

public class GUIDGenerator {
	public static String getGUID(){
		UUID uuid = UUID.randomUUID();
		long id = uuid.getMostSignificantBits();
		if(id < 0)
			id = -id;
		
		return String.valueOf(id);
	}
}
