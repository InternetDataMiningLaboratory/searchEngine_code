package com.proj.module.searchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Search {
	private static Analyzer analyzer = null;
	private static QueryParser parser = null;
	private static String keyword = null;
	
	public static void main(String[] args) throws Exception{
		  String driver = "com.mysql.jdbc.Driver";
		  String url = "jdbc:mysql://db:3306/company_service";
		  String user = "admin"; 
		  String password = "nlp506";
		  String result = null;
		  try{
			  Class.forName(driver);
			  Connection conn = DriverManager.getConnection(url, user, password);
			  if(!conn.isClosed()) 
				  System.out.println("Succeeded connecting to the Database!");
			  Statement statement = conn.createStatement();
			  String sql1 = "select * from search where search_id = 1";
			  ResultSet rs1 = statement.executeQuery(sql1);
			  rs1.next();
			  keyword = rs1.getString("search_word");
			  analyzer = new IKAnalyzer();
			  DefaultLuceneSearcher search = new DefaultLuceneSearcher(analyzer, parser);
			  TopDocs results = search.search(keyword, 1);
			  result = search.printResult(results);
			  result = result.substring(0,result.length()-1);
			  System.out.println(result);
			  String sql2 = "update search set search_result = ?";
			  PreparedStatement pst = conn.prepareStatement(sql2);
			  pst.setString(1,result);
			  pst.executeUpdate();
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	}
}
