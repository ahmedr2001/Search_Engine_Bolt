import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class WebCrawler implements Runnable {
    public static final int MAX_DEPTH = 7;
    private Thread thread;
    private String firstLink;
    private int ID;
    private ArrayList<String> visitedLinks;

    public WebCrawler(String link, int num) {
        System.out.println("WebCrawler Created");
        firstLink = link;
        ID = num;
        visitedLinks =new ArrayList<>();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        crawl(1, firstLink);
    }

    private void crawl(int level, String url) {
        if (level <= MAX_DEPTH) {
            Document document = request(url);
            if (document != null) {
                for (Element link : document.select("a[href]")) {
                    String nextLink = link.absUrl("href");
                    if (visitedLinks.contains(nextLink) == false) {
                        crawl(level++, nextLink);
                    }
                }
            }
        }
    }

    private Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                System.out.println("Bot with ID = " + ID + " Received webpage with url = " + url);
                String title = document.title();
                visitedLinks.add(url);
                return document;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Thread getThread(){
        return thread;
    }
}
