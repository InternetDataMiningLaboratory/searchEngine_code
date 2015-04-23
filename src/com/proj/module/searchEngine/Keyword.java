package com.proj.module.searchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.proj.utils.ConfigProperties;

public class Keyword {
	private static String INDEX_DIR = null;
  	private static Analyzer analyzer = null;
	private static QueryParser parser = null;
	
	public static void main(String[] args) throws Exception{
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/company_service";
		String user = "admin"; 
		String password = "nlp506";
		String keyword = null;
		ConfigProperties config =LuceneConfig.config;
		INDEX_DIR = config.getValue("lucene.indexFilePath");
		try{
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			if(!conn.isClosed()) 
				System.out.println("Succeeded connecting to the Database!");
			//String sql = "delete from model_data where model_id = 2";
			//PreparedStatement pst = conn.prepareStatement(sql);
			//pst.executeUpdate();
			String sql1 = "select * from keyword where user_id = ?";
			PreparedStatement pst1 = conn.prepareStatement(sql1);
			pst1.setString(1,args[0]);
			ResultSet rs = pst1.executeQuery();
			rs.last();  
			int rowCount = rs.getRow();
			rs.beforeFirst();
			analyzer = new IKAnalyzer(true);
			//analyzer = new KeywordAnalyzer();
			DefaultLuceneSearcher search = new DefaultLuceneSearcher(analyzer, parser);
			if(rowCount == 0)
				return;
			else{
				float score_of_one_word_r = (float) (1.0/rowCount);
				float score_of_one_word = (float)(Math.round(score_of_one_word_r*10000))/10000;
				System.out.println(score_of_one_word);
				Map<Integer,Float> map = new HashMap<Integer,Float>(); 
				while(rs.next()){
					keyword = rs.getString("keyword");
					System.out.println(keyword);
					TopDocs results = search.search(keyword, 1);
					String res = search.printResult(results);
					if(res != null){
						res = res.substring(0, res.length()-1);
						System.out.println(res);
						String[] scores = res.split(",");
						for(int i = 0; i < scores.length; i++){
							if(map.containsKey(Integer.parseInt(scores[i])))
								map.put(Integer.parseInt(scores[i]), map.get(Integer.parseInt(scores[i]))+score_of_one_word);
							else 
								map.put(Integer.parseInt(scores[i]), score_of_one_word);
						}
					}
				}
				for(Map.Entry<Integer,Float> entry:map.entrySet()){   
					System.out.println(entry.getKey()+"--->"+entry.getValue());  
					//String sql2 = "insert into model_data (model_id,data_id,data_score) values (2,?,?)";
					//PreparedStatement pst2 = conn.prepareStatement(sql2);
					//pst2.setInt(1,entry.getKey());
					//pst2.setFloat(2,entry.getValue());
					//pst2.executeUpdate();
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
