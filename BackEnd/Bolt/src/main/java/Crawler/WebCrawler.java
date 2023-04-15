package Crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler implements Runnable {

    public int MAX_LINKS_NUM = 6000;
    private Thread thread;
    private int ID;
    private ArrayList<String> visitedLinks;

    private Queue<String> bfs;

    public WebCrawler(String link, int num) {
        bfs =new LinkedList<>();
        bfs.add(link);
        ID = num;
        visitedLinks = new ArrayList<>();
        System.out.println("WebCrawler Created with ID = " + ID);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String firstLink = bfs.poll();
        crawl(firstLink);
    }

    private void crawl(String url) {
        if (visitedLinks.size() < MAX_LINKS_NUM) {
            Document document = request(url);
            if (document != null) {
                for (Element link : document.select("a[href]")) {
                    String nextLink = link.absUrl("href");
                    if (!visitedLinks.contains(nextLink)) {
                        bfs.add(nextLink);
                    }
                }
            }
            if (!bfs.isEmpty()) {
                String nextLink = bfs.poll();
                crawl(nextLink);
            }
        }
        return;
    }

    private Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                System.out.println("Bot with ID = " + ID + " Received webpage with url = " + url + "and the Title is : " + document.title());
                visitedLinks.add(url);
                return document;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Thread getThread() {
        return thread;
    }
}

