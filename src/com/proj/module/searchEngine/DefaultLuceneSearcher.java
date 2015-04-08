package com.proj.module.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.proj.utils.ConfigProperties;

public class DefaultLuceneSearcher implements ISearcher {
	
	private IndexSearcher searcher = null;
	
	private Query query = null;
	private Analyzer defaultAnalyzer = null;
	//private String field = "contents";
	//Ĭ������������÷ֵ��ֶ�(һ��Ϊÿ���ĵ��ı���������ֶ�"contents","title")
	private String[] defaultFields = {"contents","title"};
	int defaultPageSize=10000;
	public static String INDEX_STORE_PATH = null; 
	private static Directory directory = null;

	QueryParser defaultParser;

	/**����Ϊһ����ʱ����ÿ��һ��ʱ�����´�lucene�����࣬����ʵʱ����
	 * @author ZTW
	 *
	 */
	class SearcherTimer implements Runnable {

		long timeInterval = 10000;
		Date lastChangeIndexTime = new Date();

		public void run() {
			while (true) {
				Date now = new Date();
				synchronized (this) {
					long nowLong = now.getTime();
					long lastLong = lastChangeIndexTime.getTime();
					long minus = nowLong - lastLong;
					if (minus > timeInterval) {
						//IndexReader reader;
						try {
							//reader = DirectoryReader.open(LuceneContext
							//		.getContextInstance().getIndexDir());
							ConfigProperties config =LuceneConfig.config;
							INDEX_STORE_PATH = config.getValue("lucene.indexFilePath");
							directory = FSDirectory.open(new File(INDEX_STORE_PATH));
							@SuppressWarnings("deprecation")
							IndexReader reader_ = IndexReader.open(directory);
							searcher = new IndexSearcher(reader_);
							System.out.print("restart index!");
							lastChangeIndexTime = new Date();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public DefaultLuceneSearcher(Analyzer analyzer, QueryParser parser) {
		try {
			//IndexReader reader = DirectoryReader.open(LuceneContext
			//		.getContextInstance().getIndexDir());
			ConfigProperties config =LuceneConfig.config;
			INDEX_STORE_PATH = config.getValue("lucene.indexFilePath");
			directory = FSDirectory.open(new File(INDEX_STORE_PATH));
			@SuppressWarnings("deprecation")
			IndexReader reader = IndexReader.open(directory);
			searcher = new IndexSearcher(reader);

			//parser���̰߳�ȫ
			if (parser == null)
				parser = new MultiFieldQueryParser(defaultFields, analyzer);
			if (defaultAnalyzer == null)
				this.defaultAnalyzer = new StandardAnalyzer();
			this.defaultParser = parser;
			this.defaultAnalyzer = analyzer;
			//SearcherTimer st = new SearcherTimer();
			//new Thread(st).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public TopDocs search(String keyword, int page) {
		return search( keyword,  page ,defaultPageSize);
	}
	//
	/**���keyword����Ĭ�ϵ���defaultFields�����ؽ��
	 * @param keyword
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public TopDocs search(String keyword, int page,int pageSize) {

		try {
			// Ϊ�˷�ֹ���´�����ʱ�����̰߳�ȫ���������⣬�����þֲ�����
			IndexSearcher tempSearcher = searcher;
			//
			synchronized (defaultParser) {
				query = defaultParser.parse(keyword);
				//defaultParser.setDefaultOperator(QueryParser.Opertator.AND);
			}
			int start = (page - 1) * pageSize;
			int hm = start + pageSize;
            TopScoreDocCollector res = TopScoreDocCollector.create(hm, false);
            searcher.search(query, res);
 
            
            
            int rowCount = res.getTotalHits();
            int pages = (rowCount - 1) / pageSize + 1; //������ҳ��
            TopDocs results = res.topDocs(start, pageSize);
			

			// Date end = new Date();
			// System.out.println("�?��完成，用�? + (end.getTime() - start.getTime()) + "毫秒");
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//�÷�������һЩ��ǿ��ƥ������
	/**�÷������һЩ���ѯ�����Ʒ��ؽ���������"title"������"����"�ؼ�ʣ���������"author"����Ϊ"��XX"
	 * fields[]  conditionQueries[]  flags[]Ϊһһ��Ӧ�ģ�����ʵ������ʱû��flags��������flagΪmust��
	 * @param keyword
	 * @param fields
	 * @param conditionQueries
	 * @param flags
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public TopDocs search(String keyword, String fields[],String conditionQueries[],String flags[],int page,int pageSize) {
		try {
			// Ϊ�˷�ֹ���´�����ʱ�����̰߳�ȫ���������⣬�����þֲ�����
			IndexSearcher tempSearcher = searcher;
			//
			synchronized (defaultParser) {
				query = defaultParser.parse(keyword);
			}
			if(query instanceof BooleanQuery){
				for(int i=0;i<fields.length;i++){
					if(fields[i]!=null&&conditionQueries[i]!=null){
						
					TermQuery tQuery=new TermQuery(new Term(fields[i], conditionQueries[i]));
				((BooleanQuery) query).add(tQuery,org.apache.lucene.search.BooleanClause.Occur.MUST);
				
					}
				}
			}
			int start = (page - 1) * pageSize;
			int hm = start + pageSize;
            TopScoreDocCollector res = TopScoreDocCollector.create(hm, false);
            searcher.search(query, res);
 
            
            
            int rowCount = res.getTotalHits();
            int pages = (rowCount - 1) / pageSize + 1; //������ҳ��
            TopDocs results = res.topDocs(start, pageSize);
			

			// Date end = new Date();
			// System.out.println("�?��完成，用�? + (end.getTime() - start.getTime()) + "毫秒");
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String printResult(TopDocs results) {
		ScoreDoc[] h = results.scoreDocs;
		String[] result = {};
		result = new String[10001];
		if (h.length == 0) {
			System.out.println("NO RESULT!");
			return null;
		} else {
			for (int i = 0; i < h.length; i++) {
				try {
					Document doc = searcher.doc(h[i].doc);
					//System.out.println(doc.get("title"));
					result[i] = doc.get("title");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println("--------------------------");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < result.length; i++){
			if (result[i] != null)
			sb. append(result[i]+",");
		}
		String rs = sb.toString();
		rs = rs.substring(0, rs.length()-1);
		rs = rs.concat(";");
		return rs;
	}

	/**���������ת��Ϊhashmap����
	 * @param results
	 * @return
	 */
	public List returnResult(TopDocs results) {

		List<Map> resultList = new LinkedList<Map>();
		if (results == null || results.scoreDocs == null)
			return resultList;
		ScoreDoc[] h = results.scoreDocs;
		if (h.length == 0) {

		} else {
			for (int i = 0; i < h.length; i++) {
				try {
					Document doc = searcher.doc(h[i].doc);
					List<IndexableField> fields = doc.getFields();
					Map<String, String> fieldMap = new HashMap<String, String>();
					for (IndexableField f : fields) {
						fieldMap.put(f.name(), f.stringValue());
					}

					resultList.add(fieldMap);
				} catch (Exception e) {
					return resultList;
				}

			}
		}

		return resultList;
	}

	@Override
	public List<Object> searchByQueryString(String query, int page) {
		TopDocs midResults = search(query, page);

		return returnResult(midResults);
	}
	
	@Override
	public List<Object> searchByQueryString(String query, int page, int pageSize) {
		TopDocs midResults = search(query, page,pageSize);

		return returnResult(midResults);
	}
	@Override
	public List<Object> searchByQueryString(String query, String fields[],String conditionQueries[],String flags[],int page, int pageSize) {
		TopDocs midResults = search( query,  fields, conditionQueries, flags, page,  pageSize);

		return returnResult(midResults);
	}
}