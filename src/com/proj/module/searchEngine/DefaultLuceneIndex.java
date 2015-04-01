package com.proj.module.searchEngine;

import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import com.proj.utils.ConfigProperties;

public class DefaultLuceneIndex extends AbstractLuceneIndex{
	LuceneDocumentTool luceneDocument=new LuceneDocumentTool();
	static  ConfigProperties config =LuceneConfig.config;
	public DefaultLuceneIndex(Directory defaultDir,Analyzer defaultAnalyzer){
		
		try {
			// �����ļ��ı���λ��
			 dir = defaultDir;
			// ������(�ִ���)
			 analyzer = defaultAnalyzer;
			// ������
			iwc = new IndexWriterConfig(Version.LATEST, analyzer);
			iwc.setMergePolicy(optimizeIndex());
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);// ����ģʽ OpenMode.CREATE_OR_APPEND ���ģʽ
			//����������ͷ�
			if(IndexWriter.isLocked(dir))
                IndexWriter.unlock(dir);
			writer = new IndexWriter(dir, iwc);
			writer.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * �Ż�������Ż�����
	 * 
	 * @return
	 */
	private static LogMergePolicy optimizeIndex() {
		LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();

		// ����segment����ĵ�(Document)ʱ�ĺϲ�Ƶ��
		// ֵ��С,����������ٶȾͽ���
		// ֵ�ϴ�,����������ٶȾͽϿ�,>10�ʺ�������������
		// �ﵽ50���ļ�ʱ�ͺͺϲ�
		mergePolicy.setMergeFactor(50);

		// ����segment���ϲ��ĵ�(Document)��
		// ֵ��С������׷��������ٶ�
		// ֵ�ϴ�,�ʺ�������������͸�������
		mergePolicy.setMaxMergeDocs(5000);

		// ���ø���ʽ�����ļ���ʽ,�ϲ����segment
		
		return mergePolicy;
	}
	
	
	
	/* ����һƪ�ĵ���lucene���� ���ĵ�����ʽΪ��ֵ�ԣ�����("title","���ʹ����������?")
	 * @see com.proj.module.searchEngine.IDocumentIndex#addDocumentIndex(java.util.HashMap)
	 */
	@Override
	public boolean addDocumentIndex(HashMap<String, String> document) throws Exception{
		if(document==null)return true;
		Document doc=luceneDocument.getDefaultDocument(document);
		
		writer.addDocument(doc);
		writer.commit();
		// TODO Auto-generated method stub
		return true;
	}
	public boolean addDocumentIndex(Document document) throws Exception{
		if(document==null)return true;
		
		
		writer.addDocument(document);
		// TODO Auto-generated method stub
		writer.commit();
		return true;
	}
	@Override
	public boolean deleteDocumentIndex(String documentId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDocumentIndex(HashMap<String, String> document) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Analyzer getLuceneAnalyser() throws Exception {
		// TODO Auto-generated method stub
		//Analyzer analyzer = LuceneContext.getContextInstance().getAnalyzer();
		return null;
		
	}

	@Override
	public Directory getIndexDir() throws Exception {
		// TODO Auto-generated method stub

		
		return null;
	}
	

}