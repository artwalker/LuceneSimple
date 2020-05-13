package org.example.lucene;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * @author HackerStar
 * @create 2020-05-13 20:52
 */
public class LuceneSecond {

    /**
     * 索引库的添加
     * @throws Exception
     */
    @Test
    public void addDocument() throws Exception {
        //索引库存放路径
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        //创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //创建一个Document对象
        Document document = new Document();
        //向document对象中添加域
        //不同的document可以有不同的域，同一个document可以有相同的域
        document.add(new TextField("fileName", "新添加的文档", Field.Store.YES));
        document.add(new TextField("content", "新添加的文档的内容", Field.Store.NO));
        //LongPoint创建索引
        document.add(new LongPoint("size", 10000));
        //StoredField存储数据
        document.add(new StoredField("size", 10000));
        //不需要创建索引的就使用StoredField存储
        document.add(new StoredField("path", "/Users/XinxingWang/Development/Lucene/index/test.txt"));
        //添加文档到索引库
        indexWriter.addDocument(document);
        //关闭indexwriter
        indexWriter.close();
    }

    /**
     * 删除全部索引
     * @throws Exception
     */
    @Test
    public void deleteAllIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        //删除全部索引
        indexWriter.deleteAll();
        //关闭indexWriter
        indexWriter.close();
    }

    public IndexWriter getIndexWriter() throws Exception{
        //索引库存放路径
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        //创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        return indexWriter;
    }

    /**
     * 指定查询条件删除
     */
    @Test
    public void deleteByQuery() throws  Exception {
        IndexWriter indexWriter = getIndexWriter();
        //创建一个查询条件
        Query query = new TermQuery(new Term("fileName", "apache"));
        //根据查询条件删除
        indexWriter.deleteDocuments(query);
        //关闭indexwriter
        indexWriter.close();
    }

    /**
     * 更新索引库
     */
    @Test
    public void updateIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        //创建一个Document对象
        Document document = new Document();
        //向document对象中添加域
        document.add(new TextField("fileName", "要更新的文档", Field.Store.YES));
        document.add(new TextField("fileContent", "Lucene 简介 Lucene 是一个基于 Java 的全文信息检索工具包, 它不是一个完整的搜索应用程序,而是为你的应用程序提供索引和搜索功能。"
                ,Field.Store.YES));
        indexWriter.updateDocument(new Term("fileContent", "java"), document);
        //关闭indexWriter
        indexWriter.close();
    }
}
