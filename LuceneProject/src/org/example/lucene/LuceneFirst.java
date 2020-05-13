package org.example.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * @author HackerStar
 * @create 2020-05-13 17:40
 */
public class LuceneFirst {
    /**
     * 创建索引
     *
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        //1、创建一个Director对象，指定索引库保存的位置
        //把索引库保存在内存中
        //Directory directory = new RAMDirectory();
        //把索引库保存在磁盘
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        //2、基于Directory对象创建一个IndexWriter对象
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //3、读取磁盘上的文件(原始文档)，对应每个文件创建一个文档对象。
        File dir = new File("/Users/XinxingWang/Development/Lucene/searchsource");
        for (File file :
                dir.listFiles()) {
            //文件名
            String fileName = file.getName();
            //文件内容(导入commons-io-2.6.jar包到lib文件夹)
            String fileContent = FileUtils.readFileToString(file);
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(file);
            //4、创建域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            //文件名域
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            //文件内容域
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
            //文件路径域（不分析、不索引、只存储）
            Field filePathField = new TextField("filePath", filePath, Field.Store.YES);
            //文件大小域
            Field fileSizeField = new TextField("fileSize", fileSize + "", Field.Store.YES);

            //5、创建document对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);

            //6、创建索引，把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //7、关闭indexwriter对象
        indexWriter.close();
    }

    /**
     * 查询索引
     * @throws Exception
     */
    @Test
    public void searchIndex() throws Exception {
        //指定索引库存放的路径
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        //创建indexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建indexsearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询
        Query query = new TermQuery(new Term("fileName", "apache"));
        //执行查询
        //第一个参数：查询对象
        //第二个参数：查询结果返回的最大值
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("查询结果的总条数" + topDocs.totalHits);
        //遍历查询结果
        //topDocs.scoreDocs存储了document对象的id
        for (ScoreDoc scoreDoc
                : topDocs.scoreDocs
        ) {
            //scoreDoc.doc属性就是document对象的id
            //根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println("文件名：" + document.get("fileName"));
            System.out.println("文件内容：" + document.get("fileContent"));
            System.out.println("文件路径：" + document.get("filePath"));
            System.out.println("文件大小：" + document.get("fileSize"));
            System.out.println("---------------------------------------------------");
        }
        //关闭indexReader对象
        indexReader.close();
    }

    /**
     * 标准分析器分词
     * @throws Exception
     */
    @Test
    public void testTokenStream() throws Exception {
        //创建一个标准分析器对象
        Analyzer analyzer = new StandardAnalyzer();
        //获得tokenStream对象
            //第一个参数：域名（此处可以随便指定）
            //第二个参数：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("test", "千里之行，始于足下");
        //添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute((CharTermAttribute.class));
        //添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()) {
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //取关键词
            System.out.println(charTermAttribute);
            //结束位置
            System.out.println("end->" + offsetAttribute.endOffset());

        }
        tokenStream.close();
    }

    /**
     * IK分词器分词
     * @throws Exception
     */
    @Test
    public void userIK() throws Exception {
        //1、索引库存放位置
        Directory directory = FSDirectory.open(new File("/Users/XinxingWang/Development/Lucene/index").toPath());
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        //2、创建一个indexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //3、读取磁盘上的文件(原始文档)，对应每个文件创建一个文档对象。
        File dir = new File("/Users/XinxingWang/Development/Lucene/searchsource");
        for (File file :
                dir.listFiles()) {
            //文件名
            String fileName = file.getName();
            //文件内容(导入commons-io-2.6.jar包到lib文件夹)
            String fileContent = FileUtils.readFileToString(file);
            //文件路径
            String filePath = file.getPath();
            //文件大小
            long fileSize = FileUtils.sizeOf(file);
            //4、创建域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            //文件名域
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            //文件内容域
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
            //文件路径域（不分析、不索引、只存储）
            Field filePathField = new TextField("filePath", filePath, Field.Store.YES);
            //文件大小域
            Field fileSizeField = new TextField("fileSize", fileSize + "", Field.Store.YES);

            //5、创建document对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);

            //6、创建索引，把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //7、关闭indexwriter对象
        indexWriter.close();
    }
}
