package com.proj.module.searchEngine;

import com.proj.utils.ConfigProperties;

public class LuceneConfig {
	static  ConfigProperties config = new ConfigProperties("searchConfig.properties");
	public static String getDefaultIndexPath(){
		return config.getValue("lucene.indexFilePath");
	}
}