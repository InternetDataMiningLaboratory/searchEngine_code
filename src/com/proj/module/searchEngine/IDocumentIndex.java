package com.proj.module.searchEngine;

import java.util.HashMap;

import com.proj.annotation.IndexField;

public interface IDocumentIndex {
	
	public boolean addDocumentIndex(HashMap<String,String> document) throws Exception;
	public boolean deleteDocumentIndex(String documentId);
	public boolean updateDocumentIndex(HashMap<String,String> document);

}