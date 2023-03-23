import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Crawler mission begins!");
        ArrayList<WebCrawler> bots = new ArrayList<>();

        bots.add(new WebCrawler("https://news.sky.com/", 1));
        bots.add(new WebCrawler("https://www.beinsports.com/ar/", 2));
        bots.add(new WebCrawler("https://store.steampowered.com/", 3));

        for (WebCrawler crawler : bots) {
            try {
                crawler.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}