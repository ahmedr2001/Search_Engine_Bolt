package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.HashMap;

public class test {
    public static void main(String[] args) throws IOException {
        String link ="https://twitter.com/tutorialspoint";
        System.setProperty(
                "webdriver.chrome.driver",
                "./chromedriver.exe");
        ChromeOptions options =new ChromeOptions();
//        options.addArguments("--headless");
        HashMap<String, Object> images = new HashMap<String, Object>();
        images.put("images", 2);
        HashMap<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values", images);
        options.setExperimentalOption("prefs", prefs);
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://www.geeksforgeeks.org");
        Document doc = Jsoup.parse(driver.getPageSource());
        System.out.println(doc.text());
        driver.close();
    }
}
