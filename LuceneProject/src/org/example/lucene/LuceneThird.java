package org.example.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
/**
 * @author HackerStar
 * @create 2020-05-13 21:56
 */
public class LuceneThird {
    @Test
    public void testTermQuery() throws Exception {
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //创建查询对象
        Query query = new TermQuery(new Term("fileContent", "lucene"));
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        //共查询到的document个数
        System.out.println("查询结果总数量：" + topDocs.totalHits);
        //遍历查询结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("filePath"));
            System.out.println(document.get("fileSize"));
        }
        //关闭indexreader
        indexSearcher.getIndexReader().close();
    }

    public IndexSearcher getIndexSearcher() throws Exception{
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher;
    }

    public void printResult(Query query, IndexSearcher indexSearcher) throws Exception {
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        //共查询到的document个数
        System.out.println("查询结果总数量：" + topDocs.totalHits);
        //遍历查询结果
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fileName"));
            System.out.println(document.get("fileContent"));
            System.out.println(document.get("filePath"));
            System.out.println(document.get("fileSize"));
            System.out.println("-----------------------------------");
        }
        //关闭indexreader
        indexSearcher.getIndexReader().close();
    }

    @Test
    public void testRangeQuery() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = LongPoint.newRangeQuery("fileSize", 0l, 700l);
        printResult(query, indexSearcher);
    }

    @Test
    public void testQueryParser() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        //创建queryparser对象
        //第一个参数默认搜索的域
        //第二个参数就是分析器对象
        QueryParser queryParser = new QueryParser("fileContent", new IKAnalyzer());
        Query query = queryParser.parse("Lucene是java开发的");
        System.out.println(query);
        //执行查询
        printResult(query, indexSearcher);
    }

}
