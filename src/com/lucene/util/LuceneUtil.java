package com.lucene.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.lucene.LuceneConfig;
import com.lucene.data.LuceneData;
import com.model.Model;
import com.model.Novel;
import com.service.NovelService;

/**
 * lucene������
 * 
 * @author Administrator
 * 
 */
public class LuceneUtil {
	/**
	 * ��־
	 */
	static Logger logger = Logger.getLogger(LuceneUtil.class);
	
	public static Integer totalNum=0;
	

	/**
	 * ��������
	 * @param data Ҫ����������һ����¼
	 * @return
	 */
	public static synchronized boolean createIndex(LuceneData data) {
		IndexWriter indexWriter = null;
		Directory d = null;
		try {
			d = FSDirectory.open(new File(LuceneConfig.INDEX_PATH));
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36,
					AnalyzerUtil.getIkAnalyzer());
			// 3.6�Ժ��Ƽ���optimize,ʹ��LogMergePolicy�Ż�����
			conf.setMergePolicy(optimizeIndex());
			// ��������ģʽ��CREATE������ģʽ�� APPEND��׷��ģʽ
			File file = new File(LuceneConfig.INDEX_PATH);
			File[] f = file .listFiles();
			if(f.length==0)    
				conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			else
				conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND);

			indexWriter = new IndexWriter(d, conf);
			//��Ϊid��Ψһ�ģ����֮ǰ���ھ���ɾ��ԭ���ģ��ڴ����µ�
			Term term = new Term("id", data.getId());
			indexWriter.deleteDocuments(term);
			
			Document doc = getDocument(data);
			indexWriter.addDocument(doc);

			logger.debug("��������,��������{}��" + indexWriter.numDocs());
			//System.out.println("��������,��������{}��" + indexWriter.numDocs()+":"+doc.get("id")+":"+doc.get("author"));
			// �Զ��Ż��ϲ������ļ�,3.6�Ժ��Ƽ���optimize,ʹ��LogMergePolicy�Ż�����
			// indexWriter.optimize();
			indexWriter.commit();
			return true;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("����������", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} finally {
					try {
						if (d != null && IndexWriter.isLocked(d)) {
							IndexWriter.unlock(d);
						}
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("�����쳣", e);
					}
				}
			}
		}
		return false;
	}

	/**
	 * ��������
	 * 
	 * @param data
	 * @return
	 */
	public static boolean updateIndex(LuceneData data) {
		IndexWriter indexWriter = null;
		Directory d = null;
		try {
			d = FSDirectory.open(new File(LuceneConfig.INDEX_PATH));
			while (d != null && IndexWriter.isLocked(d)) {// ����ļ���ס,�ȴ�����
				Thread.sleep(1000);
				logger.error("�����Ѿ���ס�����ڵȴ�....");
			}
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_31,
					AnalyzerUtil.getIkAnalyzer());
			// 3.6�Ժ��Ƽ���optimize,ʹ��LogMergePolicy�Ż�����
			conf.setMergePolicy(optimizeIndex());

			indexWriter = new IndexWriter(d, conf);
			Term term = new Term("id", data.getId());
			// ���ܸ��������ɾ��ԭ����
			indexWriter.deleteDocuments(term);

			Document doc = getDocument(data);
			indexWriter.addDocument(doc);
			// indexWriter.optimize();

			indexWriter.commit();
			logger.debug("��������������IDΪ{}" + data.getId());
			logger.debug("��������{}��" + indexWriter.numDocs());
			return true;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("����������", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("��������쳣", e);
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} finally {
					try {
						if (d != null && IndexWriter.isLocked(d)) {
							IndexWriter.unlock(d);
						}
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("�����쳣", e);
					}
				}
			}
		}
		return false;
	}

	/**
	 * ����idɾ��������id��Ӧ������document��
	 * 
	 * @param id
	 *            document��id
	 * @return
	 */
	public static boolean deleteIndex(String id) {
		IndexWriter indexWriter = null;
		Directory d = null;
		try {
			d = FSDirectory.open(new File(LuceneConfig.INDEX_PATH));
			while (d != null && IndexWriter.isLocked(d)) {// ����ļ���ס,�ȴ�����
				Thread.sleep(1000);
				logger.error("�����Ѿ���ס�����ڵȴ�....");
			}

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_36, AnalyzerUtil.getIkAnalyzer());
			indexWriter = new IndexWriter(d, indexWriterConfig);
			Term term = new Term("id", id);
			indexWriter.deleteDocuments(term);
			// 3.6�Ժ��Ƽ���optimize,ʹ��LogMergePolicy�Ż�����
			indexWriterConfig.setMergePolicy(optimizeIndex());
			indexWriter.commit();
			logger.debug("ɾ������ID:{}������..." + id);
			logger.debug("��������{}��" + indexWriter.numDocs());
			indexWriter.close();
			return true;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			logger.error("����ɾ���쳣", e);
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
			logger.error("����ɾ���쳣", e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("����������", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("����ɾ���쳣", e);
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("�����ر��쳣", e);
				} finally {
					try {
						if (d != null && IndexWriter.isLocked(d)) {
							IndexWriter.unlock(d);
						}
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("�����쳣", e);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @param fileds Ҫ��ѯ���ۺ��ֶ� ex�� new String[]{ "contentTitle", "contentContext","keywords"};��
	 * @param occurs Ҫ��ѯ���ֶγ��ֿ��� ex��new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD };��
	 * @param keyWord Ҫ��ѯ�Ĺؼ���
	 * @param page ��ǰҳ
	 * @param pageSize ��ҳ��
	 * @return
	 */
	public static ArrayList<LuceneData> search(String[] fileds, Occur[] occurs,String keyWord,Integer page,Integer pageSize) {
		return search(fileds, occurs, keyWord,"","", page, pageSize);
	}

	/**
	 * @param fileds Ҫ��ѯ���ۺ��ֶ� ex�� new String[]{ "contentTitle", "contentContext","keywords"};��
	 * @param occurs Ҫ��ѯ���ֶγ��ֿ��� ex��new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD };��
	 * @param keyWord Ҫ��ѯ�Ĺؼ���
	 * @param subType ������
	 * @param type �������µ�������
	 * @param page  ��ǰҳ
	 * @param pageSize ��ҳ��
	 * @return
	 */
	public static ArrayList<LuceneData> search(String[] fileds, Occur[] occurs,String keyWord,String bigtype,String subType,Integer page,Integer pageSize) {
		try {
			// ---------��ʼ��---------------------------------------------------
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(LuceneConfig.INDEX_PATH)));
			IndexSearcher searcher = new IndexSearcher(reader);
			// ����������ʹ��IKSimilarity���ƶ�������
			searcher.setSimilarity(new IKSimilarity());

			// ----------���ù�����------------------------------------------------
			BooleanQuery booleanquery = new BooleanQuery();
			// �ۺϲ�ѯ   ����ѯ����1��
			Query likequery = IKQueryParser.parseMultiField(fileds, keyWord,occurs);
			booleanquery.add(likequery, Occur.MUST);
			
			//�����͹��� ����ѯ����2��
			if(bigtype.length()>0)
			{
				Query subquery = IKQueryParser.parse("bigtype", bigtype);
				booleanquery.add(subquery, Occur.MUST);
			}
			//�����͹��� ����ѯ����3��
			if(subType.length()>0)
			{
				Query subquery = IKQueryParser.parse("type", subType);
				booleanquery.add(subquery, Occur.MUST);
			}
			
			//������������
			//NumericRangeQuery<Integer> spanquery = NumericRangeQuery.newIntRange("id", begin, end, true, true);
			//booleanquery.add(spanquery, Occur.MUST);
			
			//����ʱ������(ʱ���getTime�ȴ�С)
			//NumericRangeQuery<Integer> spanquery = NumericRangeQuery.newLongRange("id", begin, end, true, true);
			//booleanquery.add(spanquery, Occur.MUST);
			
			//-------------����filter--------------------------------------------------
			
			//-------------����Ȩֵ������һ��������doc����Fieldʱfield.setBoost��--------------------
			
			//-------------����--------------------------------------------------------
			/*���ֶ�����������ǰ��Ļ��������� //true:���� false:����
			 * SortField[] sortFields = new SortField[3];
			 * SortField top = new SortField("isTop", SortField.INT, true);
			 * SortField hits = new SortField("contentHits", SortField.INT,true); 
			 * SortField pubtime = new SortField("publishTime",SortField.LONG, true); 
			 * sortFields[0] = top; 
			 * sortFields[1] = hits;
			 * sortFields[2] = pubtime; 
			 * Sort sort = new Sort(sortFields);
			 */
			
			//-------------����--------------------------------------------------------
			//��ҳ��ѯ,lucene��֧�ַ�ҳ��ѯ����Ϊ��ѯ�ٶȺܿ죬�������Ǿ����ò�ѯ����
			TopScoreDocCollector topCollector = TopScoreDocCollector.create(page*pageSize, false);//����
			searcher.search(booleanquery, topCollector);
			//��ѯ�����������
			totalNum=topCollector.getTotalHits();
			ScoreDoc[] docs = topCollector.topDocs((page - 1) * pageSize, pageSize).scoreDocs;//������������
			
			//������ʾ
			SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
			Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(booleanquery));
			highlighter.setTextFragmenter(new SimpleFragmenter(100));
			
			ArrayList<LuceneData> list = new ArrayList<LuceneData>();
			LuceneData data=null;
			for (ScoreDoc scdoc : docs) {
				Document document = searcher.doc(scdoc.doc);
				data=new LuceneData();
				//���ø߿�
				TokenStream tokenStream=null;
				String name = document.get("name");
				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "name", AnalyzerUtil.getIkAnalyzer());
				name = highlighter.getBestFragment(tokenStream, name);
				if(name==null)
					name=document.get("name");
				
				String author = document.get("author");
				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "author", AnalyzerUtil.getIkAnalyzer());
				author = highlighter.getBestFragment(tokenStream, author);			
				if(author==null)
					author=document.get("author");
				
				String outline = document.get("outline");
				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "outline", AnalyzerUtil.getIkAnalyzer());
				outline = highlighter.getBestFragment(tokenStream, outline);				
				if(outline==null)
					outline=document.get("outline");
				
				String type = document.get("type");
				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), scdoc.doc, "type", AnalyzerUtil.getIkAnalyzer());
				type = highlighter.getBestFragment(tokenStream, type);		
				if(type==null)
					type=document.get("type");

				data.setId(document.get("id"));
				data.setName(name);
				data.setAuthor(author);
				data.setOutline(outline);
				data.setType(type);
				data.setTypeid(document.get("typeid")) ;
				data.setBigtype(document.get("bigtype"));
				data.setUpdateTime(document.get("updateTime"));
				data.setImgPath(document.get("imgPath"));
				data.setImgUrlPath(document.get("imgUrlPath"));
				data.setContent(document.get("content"));
				data.setLink_url(document.get("link_url"));
				data.setHot(Long.parseLong(document.get("hot")));
				data.setClickPoint(Long.parseLong(document.get("clickPoint")));
				
				list.add(data);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("�����쳣", e);
			return new ArrayList<LuceneData>();
		}
	}

	/**
	 * �Ѵ������������ת����Document
	 * 
	 * @param data
	 * @return
	 */
	private static Document getDocument(LuceneData data) {
		Document doc = new Document();
		doc.add(new Field("id", data.getId(), Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("name", data.getName(), Store.YES, Index.ANALYZED));
		doc.add(new Field("author", data.getAuthor(), Store.YES,Index.ANALYZED));
		doc.add(new Field("outline", data.getOutline(), Store.YES,Index.ANALYZED));
		doc.add(new Field("type", data.getType(), Store.YES, Index.ANALYZED));
		doc.add(new Field("updateTime", data.getUpdateTime(), Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("imgPath", data.getImgPath(), Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("imgUrlPath", data.getImgUrlPath()==null?"":data.getImgUrlPath(), Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("content", data.getContent()==null?"":data.getContent(), Store.YES,Index.ANALYZED));
		doc.add(new Field("link_url", data.getLink_url(), Store.YES,Index.NOT_ANALYZED));

		doc.add(new Field("hot", Long.toString(data.getHot()), Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("clickPoint", Long.toString(data.getClickPoint()),Store.YES, Index.NOT_ANALYZED));
		
		doc.add(new Field("bigtype", data.getBigtype(), Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("typeid", data.getTypeid(), Store.YES,Index.NOT_ANALYZED));
		return doc;
	}

	/**
	 * �Ż������������Ż�����
	 * 
	 * @return
	 */
	private static LogMergePolicy optimizeIndex() {
		LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();

		// ����segment����ĵ�(Document)ʱ�ĺϲ�Ƶ��
		// ֵ��С,�����������ٶȾͽ���
		// ֵ�ϴ�,�����������ٶȾͽϿ�,>10�ʺ�������������
		// �ﵽ50���ļ�ʱ�ͺͺϲ�
		mergePolicy.setMergeFactor(50);

		// ����segment���ϲ��ĵ�(Document)��
		// ֵ��С������׷���������ٶ�
		// ֵ�ϴ�,�ʺ��������������͸��������
		mergePolicy.setMaxMergeDocs(5000);

		// ���ø���ʽ�����ļ���ʽ,�ϲ����segment
		// mergePolicy.setUseCompoundFile(true);
		return mergePolicy;
	}
	
	/**
	 * ת�����ͳ�lucene��data����
	 * @param list
	 * @return
	 */
	public static ArrayList<LuceneData> transformation_Novel(ArrayList<Novel> list){
		ArrayList<LuceneData> transforlist=new ArrayList<LuceneData>();
		LuceneData data=new LuceneData();
		for(Model model : list)
		{
			if(model instanceof Novel)
			{
				data=new LuceneData();
				Novel novel=(Novel)model;
				data.setId(novel.getId()+"");
				data.setName(novel.getName());
				data.setAuthor(novel.getAuthor());
				data.setOutline(novel.getOutline());
				data.setType(novel.getNovelType().getName());
				data.setTypeid(novel.getNovelType().getId()+"");
				data.setBigtype("С˵");
				data.setUpdateTime(novel.getUpdateTime()+"");
				data.setImgPath(novel.getImgPath());
				data.setImgUrlPath(novel.getImgUrlPath());
				data.setContent(novel.getContent());
				data.setLink_url(novel.getLink_url());
				data.setHot(novel.getHot());
				data.setClickPoint(novel.getClickPoint());
				transforlist.add(data);
			}
		}
		return transforlist;
	}
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args)
	{
//---------------------����
//		ApplicationContext springContext = new ClassPathXmlApplicationContext(new String[]{"classpath:com/springResource/*.xml"});
//		NovelService novelService = (NovelService)springContext.getBean("novelService"); 
//		System.out.println("novelService"+novelService);
//		
//		ArrayList<Novel> list=novelService.getNovelList(21, 100);
//		ArrayList<LuceneData> transforlist=LuceneService.transformation(list);
//		for(LuceneData data : transforlist)
//		{
//			System.out.println("in"+LuceneService.createIndex(data));
//		}
		
//---------------------����
		String[] fileds=new String[]{ "name", "author","outline","type"};
		Occur[] occurs=new Occur[] { Occur.SHOULD, Occur.SHOULD,Occur.SHOULD ,Occur.SHOULD };
		ArrayList<LuceneData> list=LuceneUtil.search(fileds, occurs, "��ѩ", 1, 10);
		
		for(LuceneData data:list)
		{
			System.out.println(data);
			System.out.println(data.getId()+":"+data.getAuthor());
		}
		System.out.println(list.size());
	}
}