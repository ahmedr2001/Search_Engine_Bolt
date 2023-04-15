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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WebCrawler implements Runnable {
    mongoDB DB;
    private final Thread thread;
    private final int ID;

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
        } catch (NoSuchAlgorithmException ignored) {
        }
    }

    private void crawl() throws NoSuchAlgorithmException {
        while (DB.getNumOfCrawledPages() + DB.getSeedSize() < mongoDB.MAX_PAGES_NUM) {
            org.bson.Document doc = DB.popSeed();
            if (doc != null) {
                Document document = request(doc);
                if (document != null) {
                    for (Element link : document.select("a[href]")) {

                        String nextLink = link.absUrl("href");
                        if (nextLink.contains("#")) {
                            nextLink = nextLink.substring(0, nextLink.indexOf("#") - 1);
                        }
                        if (nextLink.endsWith("/")) {
                            nextLink = nextLink.substring(0, nextLink.length() - 1);
                        }

                        Document jdoc = getDocument(nextLink);
                        if (jdoc != null) {
                            org.bson.Document newurl = new org.bson.Document("URL", nextLink).append("KEY", toHexString(getSHA(jdoc.body().toString())));
                            if (!DB.isCrawled(newurl) && !DB.isSeeded(newurl)) {
                                if (handleRobot(nextLink)) {
                                    DB.pushSeed(newurl);
                                    synchronized (this) {
                                        this.notifyAll();
                                    }
                                }
                            } else {
                                if (DB.getNumOfCrawledPages() + DB.getSeedSize() >= mongoDB.MAX_PAGES_NUM) return;
                                System.out.println("Link was Crawled or gonna be Seeded So skip being Seeded Again : " + nextLink);
                            }
                        }
                    }
                }
            }
        }

        while (DB.getSeedSize() != 0) {
            org.bson.Document doc = DB.popSeed();
            DB.addToCrawledPages(doc);
        }
    }

    private Document request(org.bson.Document doc) {
        try {
            String url = doc.getString("URL");
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                System.out.println("Bot with ID = " + ID + " Received webpage with url = " + url + " and the Title is : " + document.title());
                DB.addToCrawledPages(doc);
                return document;
            }
            return null;
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }
    private boolean handleRobot(String link) {
        boolean Allow = true;
        try {
            URL url = new URL(link);
            String origin = url.getProtocol() + "://" + url.getHost();
            String robot = origin + "/robots.txt";
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(robot).openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("User-agent: *"))
                    break;
            }
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Disallow: ") && link.startsWith(origin + line.substring(10))) {
                    Allow = false;
                }
                if (line.startsWith("Allow: ") && link.startsWith(origin + line.substring(8))) {
                    Allow = true;
                }
                if (line.startsWith("User-agent: ")){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Cannot Open robots.txt");
            return false;
        }
        if (!Allow) {
            System.out.println("Robot Blocked : "+link);
        }
        return Allow;
    }

    static public Document getDocument(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                return document;
            }
            return null;
        } catch (IOException | IllegalArgumentException e) {
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

