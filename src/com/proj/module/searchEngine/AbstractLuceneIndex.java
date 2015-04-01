package com.proj.module.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.proj.utils.ConfigProperties;

public abstract class AbstractLuceneIndex implements IDocumentIndex {
	

	public Directory dir;

	public Analyzer analyzer;

	public IndexWriterConfig iwc;
	public IndexWriter writer = null;
    
	
	public abstract Analyzer getLuceneAnalyser() throws Exception;
	public abstract Directory getIndexDir() throws Exception;
	
	AbstractLuceneIndex(){
		
		
		
	}
	

}