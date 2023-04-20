import DB.mongoDB;
import QueryProcessor.QueryProcessor;
import Ranker.MainRanker;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.bson.Document;

public class Main {
    public static void main(String args[]) throws IOException {
        mongoDB DB = new mongoDB("Bolt");
        // ======== Read Query ============//
        System.out.println("Enter Query");
        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();

        QueryProcessor queryProcessor = new QueryProcessor(query);
        List<Document> RelatedDocuments = queryProcessor.process();
        
        MainRanker mainRanker = new MainRanker(RelatedDocuments);
        mainRanker.runRanker(DB);
    }
}
