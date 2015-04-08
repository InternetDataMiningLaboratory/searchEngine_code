package com.proj.module.searchEngine;

import java.sql.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.proj.utils.ConfigProperties;

public class LuceneIndexBuilder_saved {
	  private static String INDEX_DIR = null;
	  private static Directory directory = null;
	  private static Analyzer analyzer = null;
	  
	  public static void main(String[] args) throws Exception{
		  String driver = "com.mysql.jdbc.Driver";
		  String url = "jdbc:mysql://db:3306/contribute_crawler";
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
			  String sql = "select * from data";
			  ResultSet rs = statement.executeQuery(sql);
			  String data_id = null;
			  String data_content = null;
			  while(rs.next()){
				  data_id = rs.getString("data_id");
				  data_content = rs.getString("data_content");
				  data_content = decodeUnicode(data_content);
				  data_content = del(data_content);
				  data_content = delTag(data_content);
				  HashMap<String, String> map = new HashMap<String, String>();
				  map.put("contents",data_content);
				  map.put("title", data_id);
				  System.out.println(data_id);
				  test.addDocumentIndex(map);
				  data_content = "";
			  }
			  rs.close();
			  conn.close();
		  } catch(Exception e) {
			  e.printStackTrace();
           	}
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
