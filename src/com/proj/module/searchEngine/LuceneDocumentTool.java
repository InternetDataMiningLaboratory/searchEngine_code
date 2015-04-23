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
	// ��Ҫ�����������ļ������һ��Document���󣬲����һ����"content"
	public Document getDefaultDocument(HashMap<String, String> sourceDocument) throws Exception {
		Document doc = new Document();
		for(Entry e:sourceDocument.entrySet()){
			Field field=getDefaultField((String)e.getKey(),(String)e.getValue());
			
		// ����ֶ�
		doc.add(field);
		}
		return doc;
	}
/*	public Document getDocument(HashMap<String, String> sourceDocument,IndexField config) throws Exception {
		Document doc = new Document();
		for(Entry e:sourceDocument.entrySet()){
			Field field=getField((String)e.getKey(),(String)e.getValue(),config);
			
		// ����ֶ�
		doc.add(field);
		}
		return doc;
	}*/
	
	public Document getDocument(List<Field> fields) throws Exception {
		Document doc = new Document();
		for(Field field:fields){
			
		// ����ֶ�
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
	/**����ָ�����������ɶ�Ӧ��field��
	 * @param config  ��bitλ�����Ӧ��������Ϣtrue or false ��6λ
	 * @return
	 */
	public FieldType getFieldType(BitSet config){
		
		FieldType fieldType = new FieldType();
		int configStart=0;
		fieldType.setIndexed(config.get(configStart++));// ����
		fieldType.setStored(config.get(configStart++));// �洢
		fieldType.setStoreTermVectors(config.get(configStart++));
		fieldType.setTokenized(config.get(configStart++));
		fieldType.setStoreTermVectorPositions(config.get(configStart++));// �洢λ��
		fieldType.setStoreTermVectorOffsets(config.get(configStart++));// �洢ƫ����
		return fieldType;
		
		
		
	}
	
	
	/*public FieldType getFieldType(IndexField config){
		
		FieldType fieldType = new FieldType();
		
		fieldType.setIndexed(config.indexed());// ����
		fieldType.setStored(config.stored());// �洢
		fieldType.setStoreTermVectors(config.storeTermVectors());
		fieldType.setTokenized(config.tokenized());
		fieldType.setStoreTermVectorPositions(config.storeTermVectorPositions());// �洢λ��
		fieldType.setStoreTermVectorOffsets(config.storeTermVectorOffsets());// �洢ƫ����
		return fieldType;
		
		
		
	}*/
	
	
}