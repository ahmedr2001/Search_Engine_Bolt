package Indexer;
import DB.mongoDB;
import org.bson.Document;
import org.jsoup.Jsoup;

import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;

public class MainIndexer {

    public static mongoDB DB;
    public static void main(String[] args) throws InterruptedException {
        DB  = new mongoDB("Bolt");
        runMainIndexer(DB);
    }

    public static void runMainIndexer(mongoDB DB) throws InterruptedException {
        System.out.println(ForkJoinPool.commonPool());
        int cnt=  0;
        int batchSize = 100;
        int iteration = 0;
        Iterator<Document> CrawledPagesCollection = DB.getCrawlerCollection(batchSize, iteration).iterator();
        System.out.println(DB.getNumOfCrawledPages());
        WebIndexer webIndexer = new WebIndexer(DB);
        while (CrawledPagesCollection.hasNext()){
            Document d = CrawledPagesCollection.next();
            String title = d.getString("TITLE");
            String page = d.getString("BODY");
            String url = d.getString("URL");
            Integer _id = cnt;
            webIndexer.startIndexer(page, title, url, _id);
            System.out.printf("index page: %d url:%s \n", cnt++, url);
            if (cnt % batchSize == 0) {
                iteration++;
                CrawledPagesCollection = DB.getCrawlerCollection(batchSize, iteration).iterator();
            }
        }
        webIndexer.updateWordsCollection();
    }

}
