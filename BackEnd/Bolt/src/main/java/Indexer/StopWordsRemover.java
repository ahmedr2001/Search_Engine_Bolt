package Indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StopWordsRemover {


    List<String> StopWords;
    public void ReadFile()  {
        File file = new File("StopWords.txt").getAbsoluteFile();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                StopWords.add(word);
                System.out.println("Stop Word: "+ word);
            }
        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public List<String> runStopWordsRemover(List<String> Words)  {
        StopWords = new ArrayList<>();
        ReadFile();
        Words.removeAll(StopWords);
        Words.removeIf(item -> item == null || "".equals(item)); // Removing blanks
        return Words;
    }
}
