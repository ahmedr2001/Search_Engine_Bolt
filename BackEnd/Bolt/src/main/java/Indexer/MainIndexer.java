package Indexer;
import DB.mongoDB;
import org.bson.Document;
import java.util.Iterator;

public class MainIndexer {

    public static mongoDB DB ;
    public static void main(String[] args){
        DB  = new mongoDB("Bolt");
        runMainIndexer(DB);
    }

    public static void runMainIndexer(mongoDB DB){
        int cnt=  0;
        Iterator<Document> CrawledPagesCollection = DB.getCrawlerCollection().iterator();
        System.out.println(DB.getNumOfCrawledPages());
        WebIndexer webIndexer = new WebIndexer();
        while (CrawledPagesCollection.hasNext()){
            Document d = CrawledPagesCollection.next();
            String page = d.getString("BODY");
            String url = d.getString("URL");
            System.out.printf("index page: %d url:%s \n", cnt++, url);
            webIndexer.startIndexer(page, url);
        }
    }

}
