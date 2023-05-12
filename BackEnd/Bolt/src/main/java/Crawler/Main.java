package Crawler;

import DB.mongoDB;
import Logging.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        mongoDB DB = new mongoDB("Bolt");
        DB.initializeSeed();
        Logging.printColored("[Start] ", Color.WHITE);
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
            bots.add(new WebCrawler(i, DB));
        }
        Logging.printColored("", Color.WHITE);
        for (WebCrawler crawler : bots) {
            try {
                String num = crawler.getThread().getName();
                crawler.getThread().join();
                System.out.println(num+" has arrived");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Crawler Mission Done");
    }
}
