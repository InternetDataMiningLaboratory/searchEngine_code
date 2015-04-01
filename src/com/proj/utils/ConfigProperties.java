package com.proj.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties {
	private  Properties props = new Properties(); 
	public ConfigProperties(String configPath){
		
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	public  String getValue(String key){
		return props.getProperty(key);
	}

    public  void updateProperties(String key,String value) {    
            props.setProperty(key, value); 
    } 
}
