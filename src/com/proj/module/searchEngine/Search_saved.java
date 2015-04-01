package com.proj.module.searchEngine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Search_saved {
	private static Analyzer analyzer = null;
	private static QueryParser parser = null;
	private static String keyword ="洁具";
	
	public static void main(String[] args) throws Exception{
		analyzer = new IKAnalyzer();
		DefaultLuceneSearcher search = new DefaultLuceneSearcher(analyzer, parser);
		TopDocs results = search.search(keyword, 1);
		search.printResult(results);
	}
}
