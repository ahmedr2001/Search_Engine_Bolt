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
        int batchSize = 1000;
        Iterator<Document> CrawledPagesCollection = DB.getCrawlerCollection().iterator();
        System.out.println(DB.getNumOfCrawledPages());
        WebIndexer webIndexer = new WebIndexer(DB);
        while (CrawledPagesCollection.hasNext()){
            Document d = CrawledPagesCollection.next();
            String page = d.getString("BODY");
            String url = d.getString("URL");
            Object id = d.get("_id");
            System.out.printf("index page: %d url:%s \n", cnt++, url);
            webIndexer.startIndexer(page, url, id);
            if (cnt % batchSize == 0) {
                webIndexer.updateLinkDB();
            }
        }
        webIndexer.updateLinkDB();
    }

}
