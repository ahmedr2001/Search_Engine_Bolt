package Crawler;

import DB.mongoDB;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        mongoDB DB = new mongoDB("Bolt");
        DB.initializeSeed();

        System.out.println("Crawler mission begins!");

        Scanner cin = new Scanner(System.in);
        int numOfThreads = -1;
        while (numOfThreads < 1) {
            System.out.println("Enter the number of threads of the crawler:");
            numOfThreads = cin.nextInt();
        }
        cin.close();

        ArrayList<WebCrawler> bots = new ArrayList<>();

        for (int i = 1; i <= numOfThreads; i++) {
            bots.add(new WebCrawler(i,DB));
        }

        for (WebCrawler crawler : bots) {
            try {
                crawler.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
