package PageRankAlgorithm;

import DB.mongoDB;
import org.bson.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static DB.mongoDB.MAX_PAGES_NUM;

public class PageRankAlgorithm {
    ArrayList<Integer> OutgoingLinks ;// Counts the number of outgoing links for each node
    double DampingFactor = 0.85;
    double [] page_rank ;
    int cnt = 0 ;
    mongoDB DB ;
    double InitialPageRank;
    Iterable<Document> crawledPages ;
    HashMap<String , Integer> URL_ID ;
    List<Document> crawledPagesLightVersion ; // Without the body
    List< HashSet<Integer>> adjList ; // Hashset to maintain uniqueness
    PageRankAlgorithm(){
        DB  = new mongoDB("Bolt");
        URL_ID = new HashMap<>();
        crawledPagesLightVersion = new ArrayList<>() ;
        int batchSize = 100 ;   Document page;
        // Adding all the pages without the body to avoid heap problems
        for(int iteration = 0 ; iteration <MAX_PAGES_NUM/batchSize ; iteration++ ){
            crawledPages = DB.getCrawlerCollection(batchSize,iteration) ;
            Iterator it = crawledPages.iterator();
            while (it.hasNext()){
                page = (Document) it.next();
                page.remove("BODY");
                URL_ID.put(page.getString("URL"), cnt);
                page.append("ID" , cnt++) ;
                crawledPagesLightVersion.add(page) ;
            }
        }
        init();
        page_rank = new double[cnt] ;
        for (int i = 0 ; i<cnt ; i++){
            addChildren(i);
        }
        InitialPageRank = 1 /(double)(cnt-1);
        for (int k = 0; k < cnt; k++) page_rank[k] = InitialPageRank;

        System.out.printf("\n Initial PageRank Values , 0th Step \n");
        for (int k = 0; k <cnt; k++) {
            System.out.printf(" Page Rank of " + k + " is :\t" + page_rank[k] + "\n");
        }
        int ITERATION_STEP = 100 , k ;
        double []old_page_rank  = new double[cnt] ;
        while (ITERATION_STEP-- >0) // Iterations
        {
            // Store the PageRank for All Nodes in Temporary Array
            for ( k = 0; k < cnt; k++) {
                old_page_rank[k] = page_rank[k];
                page_rank[k] = 0.0;
            }
            for (int internalNode = 0; internalNode < cnt; internalNode++) {
                for(Integer externalNode : adjList.get(internalNode)){
                    if(internalNode == externalNode) continue;
                    page_rank[internalNode] += old_page_rank[externalNode] * (1 / (double)OutgoingLinks.get(externalNode));
                }
            }
            System.out.printf("\n After " + ITERATION_STEP + "th Step \n");
            for (k = 0; k < cnt; k++)
                System.out.printf(" Page Rank of " + k + " is :\t" + page_rank[k] + "\n");
        }
        // Add the Damping Factor to PageRank
        for (k = 0; k <cnt; k++) {
//            page_rank[k] = (1 - DampingFactor) + DampingFactor * page_rank[k];
            DB.addPageRank(crawledPagesLightVersion.get(k).getString("URL") , page_rank[k]);
        }
        System.out.printf("\n Final Page Rank : \n");
        for (k = 0; k < cnt; k++) {
            System.out.printf(" Page Rank of " + k + " is :\t" + page_rank[k] + "\n");
        }

    }
    public static void main(String [] args){
        PageRankAlgorithm pageRankAlgorithm = new PageRankAlgorithm();
    }
    public void init(){
        adjList=new ArrayList<>() ;
        OutgoingLinks = new ArrayList<Integer>();
        for(int e=0;e<cnt;e++)
        {
            HashSet<Integer> temp =new HashSet<>();
            adjList.add(temp);
        }
    }

    public void addChildren(int idx){
        String url  = crawledPagesLightVersion.get(idx).getString("URL" );
        org.jsoup.select.Elements children = getChildren(url) ;
        for(Element link : children){
            String child_url  = link.absUrl("href") ;
            if(URL_ID.get(child_url) == null) continue; // If it's not in our crawled pages we don't care
            adjList.get(idx).add(URL_ID.get(child_url));  // It means there is edge from parent to child
        }
        OutgoingLinks.add(adjList.get(idx).size()+1);
    }
    public org.jsoup.select.Elements getChildren(String url) {
            String body = DB.getUrlBody(url) ;
            org.jsoup.nodes.Document doc = Jsoup.parse(body);
            return doc.select("a[href]");
    }

}
