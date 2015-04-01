package com.proj.module.searchEngine;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.util.Version;

/**搜索策略，策略模式实现，封装了具体的搜索类
 * @author ZTW
 * 
 *
 */
public class SearchStrategy implements ISearcher{
	ISearcher searcherInstance;
    public final static int TYPE_MULTI_FIELD=1;
    Analyzer defaultAnalyzer = LuceneContext.getContextInstance().getAnalyzer();
    String[] fields={"title","content"};
    public SearchStrategy(int searchType){
    	switch(searchType){
    		case TYPE_MULTI_FIELD:
    			{MultiFieldQueryParser multiParser=new MultiFieldQueryParser(fields, defaultAnalyzer);
    			searcherInstance=new DefaultLuceneSearcher(defaultAnalyzer, multiParser);}
    	
    	}
    }
	
	
	@Override
	public List<Object> searchByQueryString(String query, int page) {
		if(searcherInstance!=null)
			return searcherInstance.searchByQueryString(query, page);
		return null;
	}
	@Override
	public List<Object> searchByQueryString(String query, int page,int pageSize) {
		if(searcherInstance!=null)
			return searcherInstance.searchByQueryString(query, page,pageSize);
		return null;
	}
	@Override
	public List<Object> searchByQueryString(String query, String fields[],String conditionQueries[],String flags[],int page, int pageSize){
		if(searcherInstance!=null)
			return searcherInstance.searchByQueryString(query, fields,conditionQueries,flags,page,pageSize);
		return null;
	}
}


