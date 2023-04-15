package DB;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class mongoDB {

    public static int MAX_PAGES_NUM = 6000;
    private static MongoClient client;
    private static MongoDatabase DB;
    MongoCollection<Document> seedCollection;
    MongoCollection<Document> crawlerCollection;

    //    public static void main(String[] args) {
//        System.out.println("Hello world!");
//        MongoClient client = MongoClients.create("mongodb+srv://ahmedr2001:eng3469635@javasearchengine.8xarqeo.mongodb.net/?retryWrites=true&w=majority");
//
//        MongoDatabase db = client.getDatabase("MangaDB");
//        System.out.println("Database connected !");
//        MongoCollection col = db.getCollection("Mangas");
//
//        Document sampleDoc = new Document().append("name", "Manga");
//
//        col.insertOne(sampleDoc);
//
//        List_All(col);
//
//    }
    public mongoDB(String DB_Name) {
        if (client == null) {
            ConnectionString connectionString = new ConnectionString("mongodb+srv://ahmedr2001:eng3469635@javasearchengine.8xarqeo.mongodb.net/?retryWrites=true&w=majority");
            try (MongoClient mongoClient = MongoClients.create(connectionString)) {
                client = mongoClient;
                DB = mongoClient.getDatabase(DB_Name);
                seedCollection = DB.getCollection("Seed");
                crawlerCollection = DB.getCollection("CrawledPages");
            } catch (Exception e) {
                System.out.println("Connection to mongoDB failed" + e);
            }
        } else {
            System.out.println("Already connected to the client");
        }

    }

    public void initializeSeed() {
        if (crawlerCollection.countDocuments() >= MAX_PAGES_NUM) {
            System.out.println("Crawling has reached its limit which is equal to " + MAX_PAGES_NUM + " ,System is rebooting");
            crawlerCollection.drop();
            seedCollection.drop();
        }
        if (seedCollection.countDocuments() == 0) {
            try {
                File file = new File("seed.txt");
                Scanner cin = new Scanner(file);
                while (cin.hasNextLine()) {
                    Document url = new Document("URL", cin.nextLine());
                    seedCollection.insertOne(url);
                }
                cin.close();
            } catch (FileNotFoundException e) {
                System.out.println("Reading seed file failed :" + e);
            }
        } else {
            System.out.println("Crawling hasn't reached its limit yet , so System is Continued");
        }
    }

    public void addToCrawledPages(Document doc) {
        crawlerCollection.insertOne(doc);
    }

    public boolean isCrawled(String url) {
        return crawlerCollection.find(new org.bson.Document("URL", url)).iterator().hasNext();
    }

    public void pushSeed(Document doc) {
        seedCollection.insertOne(doc);
    }

    public Document popSeed() {
        return seedCollection.findOneAndDelete(new Document());
    }

    public boolean isSeeded(String url) {
        return seedCollection.find(new org.bson.Document("URL", url)).iterator().hasNext();
    }

    public long getSeedSize() {
        return seedCollection.countDocuments();
    }

    public long getNumOfCrawledPages() {
        return crawlerCollection.countDocuments();
    }

    public static void List_All(MongoCollection collection) {
//        Listing All Mongo Documents in Collection
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;
// Getting the iterator
        System.out.println("Listing All Mongo Documents");
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
//specific document retrieving in a collection
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name", "Manga");
        System.out.println("Retrieving specific Mongo Document");
        MongoCursor<Document> cursor = collection.find(searchQuery).iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }

}
