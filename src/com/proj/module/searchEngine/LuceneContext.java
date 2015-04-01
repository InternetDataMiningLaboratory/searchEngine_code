package com.proj.module.searchEngine;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**该类为lucene上下文，包含了一下常用的工具类
 * @author ZTW
 *
 */
public class LuceneContext {
	static Object lock=new Object();
	Directory indexDir ;
	Analyzer analyzer ;
	private static LuceneContext contextInstance=new LuceneContext();
	DefaultLuceneIndex defaultLuceneIndex;
	public static LuceneContext getContextInstance() {
		synchronized (lock) {
			if(contextInstance==null)contextInstance=new LuceneContext();
			
		}
		return contextInstance;
		
		
	}
	public static void setContextInstance(LuceneContext contextInstance) {
		LuceneContext.contextInstance = contextInstance;
	}

	
	public DefaultLuceneIndex getDefaultLuceneIndex() {
		return defaultLuceneIndex;
	}
	public void setDefaultLuceneIndex(DefaultLuceneIndex defaultLuceneIndex) {
		this.defaultLuceneIndex = defaultLuceneIndex;
	}
	private LuceneContext(){
		try{
		 indexDir = this.getIndexDir();
		}
		catch(Exception e){
			
		}
		 analyzer = new IKAnalyzer();
		
		 defaultLuceneIndex=new DefaultLuceneIndex(indexDir,analyzer);
	}
	public Analyzer getAnalyzer() {
		return analyzer;
	}
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	public void setIndexDir(Directory indexDir) {
		this.indexDir = indexDir;
	}
	
	public Directory getIndexDir() throws Exception {
		// TODO Auto-generated method stub
		if(indexDir!=null)return indexDir;
		File file=new File(LuceneConfig.getDefaultIndexPath());
		if(!file.exists()){
			
			file.mkdirs();
		}
		indexDir = FSDirectory.open(file);
		
		return indexDir;
	}
}