package com.proj.module.searchEngine;

import java.util.*;

import org.apache.lucene.search.BooleanClause.Occur;

public interface ISearcher {
	
	/**把用户输入的字符串直接传入进行搜索
	 * @return
	 */
	public List<Object> searchByQueryString(String query,int page);
	public List<Object> searchByQueryString(String query,int page,int pageSize);
	
	
	
	
	/**flag值参考	 public static final Occur MUST;
    public static final Occur SHOULD;
    public static final Occur MUST_NOT;
	 */

	public List<Object> searchByQueryString(String query, String fields[],String conditionQueries[],String flags[],int page, int pageSize);

}