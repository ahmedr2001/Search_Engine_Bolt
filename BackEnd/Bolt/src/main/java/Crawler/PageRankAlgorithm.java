package Crawler;

import DB.mongoDB;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static DB.mongoDB.MAX_PAGES_NUM;

public class PageRankAlgorithm {
    double OutgoingLinks = 0;
    double DampingFactor = 0.85;
    Iterable<Document> crawledPages ;
    List<Document> crawledPagesLightVersion ; // Without the body
    PageRankAlgorithm(mongoDB DB){
        crawledPagesLightVersion = new ArrayList<>() ;
        int batchSize = 100 ;   Document page;
        // Adding all the pages without the body to avoid heap problems
        for(int iteration = 0 ; iteration <=MAX_PAGES_NUM/batchSize ; iteration++ ){
            crawledPages = DB.getCrawlerCollection(batchSize,iteration) ;
            Iterator it = crawledPages.iterator();
            while (it.hasNext()){
                page = (Document) it.next();
                page.remove("BODY");
                crawledPagesLightVersion.add(page) ;
            }
        }
        int N = crawledPagesLightVersion.size() ;
        List<List<Integer>> adjMatrix=new ArrayList<List<Integer>>(N);



    }
}
