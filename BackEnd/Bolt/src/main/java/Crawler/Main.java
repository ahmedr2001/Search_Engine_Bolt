package Crawler;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Crawler mission begins!");

        Scanner in = new Scanner(System.in);
        int numOfThreads=-1;
        while  (numOfThreads < 1){
            System.out.println("Enter the number of threads of the crawler:");
            numOfThreads = in.nextInt();
            in.close();
        }


        ArrayList<WebCrawler> bots = new ArrayList<WebCrawler>();

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
