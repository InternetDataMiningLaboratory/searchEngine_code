package com.proj.module.searchEngine;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

//import com.proj.annotation.IndexField;

public class LuceneDocumentTool {
	// 将要建立索引的文件构造成一个Document对象，并添加一个域"content"
	public Document getDefaultDocument(HashMap<String, String> sourceDocument) throws Exception {
		Document doc = new Document();
		for(Entry e:sourceDocument.entrySet()){
			Field field=getDefaultField((String)e.getKey(),(String)e.getValue());
			
		// 添加字段
		doc.add(field);
		}
		return doc;
	}
/*	public Document getDocument(HashMap<String, String> sourceDocument,IndexField config) throws Exception {
		Document doc = new Document();
		for(Entry e:sourceDocument.entrySet()){
			Field field=getField((String)e.getKey(),(String)e.getValue(),config);
			
		// 添加字段
		doc.add(field);
		}
		return doc;
	}*/
	
	public Document getDocument(List<Field> fields) throws Exception {
		Document doc = new Document();
		for(Field field:fields){
			
		// 添加字段
		doc.add(field);
		}
		return doc;
	}
	
	
	
	public Field getDefaultField(String fieldName,String text){
		BitSet config=new BitSet();
		config.set(0, 7);
		//config.set(1, false);
		FieldType defaultFieldType=getFieldType(config);
		Field field = new Field(fieldName, text, defaultFieldType);
		return field;
	}
	/*public Field getField(String fieldName,String text,IndexField config){
		
		FieldType fieldType=getFieldType(config);
		Field field = new Field(fieldName, text, fieldType);
		return field;
	}*/
	/**根据指定的配置生成对应的field域
	 * @param config  用bit位代表对应的配置信息true or false 共6位
	 * @return
	 */
	public FieldType getFieldType(BitSet config){
		
		FieldType fieldType = new FieldType();
		int configStart=0;
		fieldType.setIndexed(config.get(configStart++));// 索引
		fieldType.setStored(config.get(configStart++));// 存储
		fieldType.setStoreTermVectors(config.get(configStart++));
		fieldType.setTokenized(config.get(configStart++));
		fieldType.setStoreTermVectorPositions(config.get(configStart++));// 存储位置
		fieldType.setStoreTermVectorOffsets(config.get(configStart++));// 存储偏移量
		return fieldType;
		
		
		
	}
	
	
	/*public FieldType getFieldType(IndexField config){
		
		FieldType fieldType = new FieldType();
		
		fieldType.setIndexed(config.indexed());// 索引
		fieldType.setStored(config.stored());// 存储
		fieldType.setStoreTermVectors(config.storeTermVectors());
		fieldType.setTokenized(config.tokenized());
		fieldType.setStoreTermVectorPositions(config.storeTermVectorPositions());// 存储位置
		fieldType.setStoreTermVectorOffsets(config.storeTermVectorOffsets());// 存储偏移量
		return fieldType;
		
		
		
	}*/
	
	
}