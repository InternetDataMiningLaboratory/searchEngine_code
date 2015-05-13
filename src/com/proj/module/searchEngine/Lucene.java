package com.proj.module.searchEngine;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;
import com.proj.utils.ConfigProperties;
import org.json.JSONArray;

public class Lucene {
	  	//private static String INDEX_DIR = "/home/b50601/LuceneIndex";
		private static String INDEX_DIR = null;
	  	private static Directory directory = null;
	  	private static Analyzer analyzer = null;
		private static QueryParser parser = null;
		private static String keyword = null;

		public static void main(String[] args) throws Exception{
			if (args[0].equals("patch")){
				  String driver = "com.mysql.jdbc.Driver";
				  String url = "jdbc:mysql://mysql.service.consul/contribute_crawler";
				 // String url = "jdbc:mysql://172.16.153.34/contribute_crawler";
				  String user = "admin"; 
				  String password = "nlp506";
				  ConfigProperties config =LuceneConfig.config;
				  INDEX_DIR = config.getValue("lucene.indexFilePath");
				  directory = FSDirectory.open(new File(INDEX_DIR));
				  analyzer = new IKAnalyzer();
				  DefaultLuceneIndex test =new DefaultLuceneIndex(directory, analyzer);
				  try{
					  Class.forName(driver);
					  Connection conn = DriverManager.getConnection(url, user, password);
					  if(!conn.isClosed()) 
						  System.out.println("Succeeded connecting to the Database!");
					  Statement statement = conn.createStatement();
					  String sql1 = "select * from patch where patch_id = ?"; 
					  PreparedStatement pst = conn.prepareStatement(sql1);
					  pst.setString(1,args[1]);
					  ResultSet rs1 = pst.executeQuery();
					  rs1.next();
					  String patch_set = rs1.getString("patch_set");
					  JSONArray jsonArray = new JSONArray(patch_set);
					  for(int i = 0; i < jsonArray.length(); i++){
						  String set = jsonArray.getString(i);
						  System.out.println(set);
						  String sql2 = "select * from data where data_validate = ?";
						  PreparedStatement pst2 = conn.prepareStatement(sql2);
						  pst2.setString(1,set);
						  ResultSet rs2 = pst2.executeQuery();
						  rs2.next();
						  String data_id = rs2.getString("data_id");
						  String data_content = rs2.getString("data_content");
						  data_content = decodeUnicode(data_content);
						  data_content = del(data_content);
						  data_content = delTag(data_content);
						  HashMap<String, String> map = new HashMap<String, String>();
						  map.put("contents",data_content);
						  map.put("title", data_id);
						  test.addDocumentIndex(map);
						  data_content = "";
						  rs2.close();
					  }
					  String sql3 = "update patch set patch_status = ? where patch_id = ?";
					  PreparedStatement pst1 = conn.prepareStatement(sql3);
					  pst1.setString(1,"Success");
					  pst1.setString(2,args[1]);
					  pst1.executeUpdate();
					  rs1.close();
					  conn.close();
				  } catch(Exception e) {
					  String err = e.getMessage();
					  Connection conn = DriverManager.getConnection(url, user, password);
					  String sql3 = "update patch set patch_status = ? where patch_id = ?";
					  PreparedStatement pst1 = conn.prepareStatement(sql3);
					  pst1.setString(1,"Error:"+err);
					  pst1.setString(2,args[1]);
					  pst1.executeUpdate();
		           	}
			}
			else if (args[0].equals("search")){
				  String driver = "com.mysql.jdbc.Driver";
				  String url = "jdbc:mysql://mysql.service.consul/company_service";
				  //String url = "jdbc:mysql://172.16.153.34/company_service";
				  String user = "admin"; 
				  String password = "nlp506";
				  String result = "";
				  ConfigProperties config =LuceneConfig.config;
				  INDEX_DIR = config.getValue("lucene.indexFilePath");
				  
				  try{
					  Class.forName(driver);
					  Connection conn = DriverManager.getConnection(url, user, password);
					  if(!conn.isClosed()) 
						  System.out.println("Succeeded connecting to the Database!");
					  String sql1 = "select * from search where search_id = ?";
					  PreparedStatement pst1 = conn.prepareStatement(sql1);
					  pst1.setString(1,args[1]);
					  ResultSet rs1 = pst1.executeQuery();
					  rs1.next();
					  String keyword = rs1.getString("search_word");
					  JSONArray jsonArray = new JSONArray(keyword);
					  analyzer = new IKAnalyzer(true);
					  DefaultLuceneSearcher search = new DefaultLuceneSearcher(analyzer, parser);
					  for (int i = 0; i < jsonArray.length(); i++){
						  System.out.println(jsonArray.getString(i));
						  TopDocs results = search.search(jsonArray.getString(i), 1);
						  String res = search.printResult(results);
						  result = result+"\""+jsonArray.getString(i)+"\":"+res;
					  }
					  result = result.substring(0, result.length()-1);
					  result = "{"+result+"}";
					  System.out.println(result);
					  String sql2 = "update search set search_result = ? where search_id = ?";
					  PreparedStatement pst = conn.prepareStatement(sql2);
					  pst.setString(1,result);
					  pst.setString(2,args[1]);
					  pst.executeUpdate();
					  String sql3 = "update search set search_status = ? where search_id = ?";
					  PreparedStatement pst2 = conn.prepareStatement(sql3);
					  pst2.setString(1,"Success");
					  pst2.setString(2,args[1]);
					  pst2.executeUpdate();
				  }catch(Exception e) {
					  String err = e.getMessage();
					  Connection conn = DriverManager.getConnection(url, user, password);
					  String sql3 = "update search set search_status = ? where search_id = ?";
					  PreparedStatement pst2 = conn.prepareStatement(sql3);
					  pst2.setString(1,"Error:"+err);
					  pst2.setString(2,args[1]);
					  pst2.executeUpdate();
				  }
			}
			else
				System.out.println("Wrong Input!");
		}
		
		  public static String decodeUnicode(String theString) {    
			  char aChar;    
			  int len = theString.length();    
			  StringBuffer outBuffer = new StringBuffer(len);    
			  for (int x = 0; x < len;) {    
				  aChar = theString.charAt(x++);    
				  if (aChar == '\\') {    
					  aChar = theString.charAt(x++);    
					  if (aChar == 'u') {    
						  int value = 0;    
						  for (int i = 0; i < 4; i++) {    
							  aChar = theString.charAt(x++);    
							  switch (aChar) {    
							  case '0':    
							  case '1':    
							  case '2':    
							  case '3':    
							  case '4':    
							  case '5':    
							  case '6':    
							  case '7':    
							  case '8':    
							  case '9':    
								  value = (value << 4) + aChar - '0';    
								  break;    
							  case 'a':    
							  case 'b':    
							  case 'c':    
							  case 'd':    
							  case 'e':    
							  case 'f':    
								  value = (value << 4) + 10 + aChar - 'a';    
								  break;    
							  case 'A':    
							  case 'B':    
							  case 'C':    
							  case 'D':    
							  case 'E':    
							  case 'F':    
								  value = (value << 4) + 10 + aChar - 'A';    
								  break;    
							  default:    
								  throw new IllegalArgumentException(    
										  "Malformed   \\uxxxx   encoding.");    
							  }    
						  }    
						  outBuffer.append((char) value);    
					  } else {    
						  if (aChar == 't')    
							  aChar = '\t';    
						  else if (aChar == 'r')    
							  aChar = '\r';    
						  else if (aChar == 'n')    
							  aChar = '\n';    
						  else if (aChar == 'f')    
							  aChar = '\f';    
						  outBuffer.append(aChar);    
					  }    
				  } else   
					  outBuffer.append(aChar);    
			  }    
			  return outBuffer.toString();    
		  }
		  
		  public static String del(String cont){
			  cont = cont.replace("{", "");
			  cont = cont.replace("}", "");
			  cont = cont.replace("\"", "");
			  return cont;
		  }
		  
		  public static String delTag(String htmlStr){ 
		      String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //����script��������ʽ 
		      String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //����style��������ʽ 
		      String regEx_html="<[^>]+>"; //����HTML��ǩ��������ʽ 
		         
		      Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
		      Matcher m_script=p_script.matcher(htmlStr); 
		      htmlStr=m_script.replaceAll(""); //����script��ǩ 
		         
		      Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
		      Matcher m_style=p_style.matcher(htmlStr); 
		      htmlStr=m_style.replaceAll(""); //����style��ǩ 
		         
		      Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
		      Matcher m_html=p_html.matcher(htmlStr); 
		      htmlStr=m_html.replaceAll(""); //����html��ǩ 

		      return htmlStr.trim(); //�����ı��ַ� 
		  } 
}
