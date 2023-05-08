package Crawler;

import DB.mongoDB;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WebCrawler implements Runnable {

    public static boolean done = false;
    mongoDB DB;
    private final Thread thread;
    private final int ID;

    private final int THRESHOLD = 100;

    public WebCrawler(int num, mongoDB DB) {
        this.DB = DB;
        ID = num;
        System.out.println("WebCrawler Created with ID = " + ID);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(ID + " finished");
    }

    private void crawl() throws NoSuchAlgorithmException {
        while (DB.getNumOfCrawledPages() + DB.getSeedSize() < mongoDB.MAX_PAGES_NUM + THRESHOLD) {
            if (DB.getNumOfCrawledPages() == mongoDB.MAX_PAGES_NUM) return;
            org.bson.Document doc = DB.popSeed();
            if (doc != null) {
                Document document = request(doc);
                if (document != null) {
                    if (DB.getNumOfCrawledPages() + DB.getSeedSize() >= mongoDB.MAX_PAGES_NUM + THRESHOLD) break;
                    for (Element link : document.select("a[href]")) {
                        if (DB.getNumOfCrawledPages() + DB.getSeedSize() >= mongoDB.MAX_PAGES_NUM + THRESHOLD) break;
                        String nextLink = link.absUrl("href");
                        if (nextLink.contains("?")) {
                            int index = nextLink.indexOf("?");
                            nextLink = nextLink.substring(0, index);
                        }
                        if (nextLink.contains("#")) {
                            nextLink = nextLink.substring(0, nextLink.indexOf("#") - 1);
                        }
                        if (nextLink.endsWith("/")) {
                            nextLink = nextLink.substring(0, nextLink.length() - 1);
                        }

                        Document jdoc = getDocument(nextLink);
                        if (jdoc != null) {
                            org.bson.Document newurl = new org.bson.Document("URL", nextLink).append("KEY", toHexString(getSHA(jdoc.body().toString()))).append("BODY", jdoc.body().toString()).append("TITLE", jdoc.title());
                            if (!DB.isCrawled(newurl) && !DB.isSeeded(newurl)) {
                                if (handleRobot("*", nextLink, ID)) {
                                    DB.pushSeed(newurl);
                                }
                            } else {
                                if (DB.getNumOfCrawledPages() + DB.getSeedSize() >= mongoDB.MAX_PAGES_NUM + THRESHOLD)
                                    break;
                                System.out.println(ID + "=>Link was Crawled or gonna be Seeded So skip being Seeded Again : " + nextLink);
                            }
                        }
                    }
                }
            }
        }
        done = true;
        synchronized (this) {
            while (DB.getNumOfCrawledPages() < mongoDB.MAX_PAGES_NUM) {
                org.bson.Document doc = DB.popSeed();
                DB.addToCrawledPages(doc, ID);
                if (DB.getSeedSize() == 0) {
                    return;
                }
            }
        }
    }

    private Document request(org.bson.Document doc) {
        try {
            if (DB.getNumOfCrawledPages() + DB.getSeedSize() >= mongoDB.MAX_PAGES_NUM + THRESHOLD) return null;
            String url = doc.getString("URL");
            if (url.contains("pinterest")) {
                return null;
            }
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                if (!DB.isCrawled(doc) && !DB.isSeeded(doc)) {
                    DB.addToCrawledPages(doc, ID);
                    return document;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean handleRobot(String useragent, String link, int ID) {
        if (link.contains("pinterest")) {
            return false;
        }
        try {
            if (done) return false;
            URL url = new URL(link);
            String host = url.getHost();
            URL robotUrl = new URL("https://" + host + "/robots.txt");
            URLConnection robotConn = robotUrl.openConnection();
            BufferedReader robotReader = new BufferedReader(new InputStreamReader(robotConn.getInputStream()));
            String line;
            boolean matched = false;
            while ((line = robotReader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("User-agent: ")) {
                    String pattern = line.substring(12);
                    if (pattern.equals("*") || pattern.equals(useragent)) {
                        matched = true;
                    } else {
                        matched = false;
                    }
                }
                if (matched) {
                    if (line.startsWith("Disallow: ")) {
                        String path = line.substring(10);
                        if (link.contains(path)) {
                            System.out.println(ID + " => Robot.txt Blocked : " + link);
                            return false;
                        }
                    }
                }
            }
            robotReader.close();
        } catch (Exception e) {
            System.out.println(ID + "=> Robot.txt not found : " + link);
        }
        return true;
    }

    static public Document getDocument(String url) {
        if (url.contains("pinterest")) {
            return null;
        }
        try {
            if (done) return null;
            if (url.contains("?")) {
                int index = url.indexOf("?");
                url = url.substring(0, index);
            }
            if (url.contains("#")) {
                url = url.substring(0, url.indexOf("#") - 1);
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                return document;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public Thread getThread() {
        return thread;
    }
}

