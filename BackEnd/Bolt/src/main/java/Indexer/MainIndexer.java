package Indexer;
import DB.mongoDB;
import org.bson.Document;
import org.jsoup.Jsoup;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class MainIndexer {
    static final int TH_SZ = 8;
    public static mongoDB DB;
    public static void main(String[] args) throws InterruptedException {
        DB  = new mongoDB("Bolt");
        runMainIndexer(DB);
    }

    public static void runMainIndexer(mongoDB DB) throws InterruptedException {
        System.out.println(DB.getNumOfCrawledPages());
        class RunMainIndexer implements Runnable {
            int cnt = (int) DB.getNumberOfIndexedUrls();
            int batchSize = 10;
            int iteration = cnt / batchSize - 1;
            List<Thread> thArr = new ArrayList<Thread>();
            public RunMainIndexer() throws InterruptedException {
                System.out.printf("indexed pages: %d\n", cnt);
                for (int i = 0; i < TH_SZ; i++) {
                    Thread th = new Thread(this);
                    String I = Integer.toString(i, 10);
                    th.setName(I);
                    thArr.add(th);
                }
                for (Thread th : thArr) {
                    th.start();
                }

                for (Thread th : thArr) {
                    th.join();
                }
            }
            WebIndexer webIndexer = new WebIndexer(DB);
            public void run() {
                try {
                    Iterator<Document> CrawledPagesCollection;
                    Integer localIteration = -1;
                    synchronized (this) {
                        iteration++;
                        localIteration = iteration;
                        System.out.printf("Thread %s updating iteration first block, iteration = %d\n", Thread.currentThread().getName(), localIteration);
                    }
                    CrawledPagesCollection = DB.getCrawlerCollection(batchSize, localIteration).iterator();
                    Document crawledPageDoc;
                    String title, url, pageContent;
                    Integer _id;
                    while (CrawledPagesCollection.hasNext()) {
                        crawledPageDoc = CrawledPagesCollection.next();
                        title = crawledPageDoc.getString("TITLE");
                        url = crawledPageDoc.getString("URL");
                        pageContent = crawledPageDoc.getString("BODY");
                        synchronized (this) {
                            cnt++;
                            _id = cnt;
                        }
                        System.out.printf("index page: %d url:%s \n", _id, url);
                        webIndexer.startIndexer(pageContent, title, url, _id);
                        if (!CrawledPagesCollection.hasNext()) {
                            synchronized (this) {
                                iteration++;
                                localIteration = iteration;
                                System.out.printf("Thread %s updating iteration second block, iteration = %d, _id = %d\n", Thread.currentThread().getName(), localIteration, _id);
                            }
                            CrawledPagesCollection = DB.getCrawlerCollection(batchSize, localIteration).iterator();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        RunMainIndexer runMainIndexer = new RunMainIndexer();
    }

}
