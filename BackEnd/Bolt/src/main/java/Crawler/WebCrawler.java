package Crawler;

import DB.mongoDB;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class WebCrawler implements Runnable {

    mongoDB DB;
    private Thread thread;
    private int ID;

    public WebCrawler(int num, mongoDB DB) {
        this.DB = DB;
        ID = num;
        System.out.println("WebCrawler Created with ID = " + ID);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        crawl();
    }

    private void crawl() {
        while (DB.getNumOfCrawledPages() < mongoDB.MAX_PAGES_NUM) {
            org.bson.Document doc = DB.popSeed();
            if (doc != null) {
                Document document = request(doc.getString("URL"));
                if (document != null) {
                    for (Element link : document.select("a[href]")) {
                        String nextLink = link.absUrl("href");
                        if (!(DB.isCrawled(nextLink) || DB.isSeeded(nextLink))) {
                            org.bson.Document newurl = new org.bson.Document("URL", nextLink);
                            DB.pushSeed(newurl);
                            synchronized (this) {
                                this.notifyAll();
                            }
                        } else {
                            System.out.println("Link was Crawled or Seeded before : " + nextLink);
                        }
                    }
                }
            } else {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                System.out.println("Bot with ID = " + ID + " Received webpage with url = " + url + "and the Title is : " + document.title());
                org.bson.Document newurl = new org.bson.Document("URL", url);
                DB.addToCrawledPages(newurl);
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

