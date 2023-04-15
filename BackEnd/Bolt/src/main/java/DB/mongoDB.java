package DB;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.Iterator;
public class mongoDB {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MongoClient client = MongoClients.create("mongodb+srv://ahmedr2001:eng3469635@javasearchengine.8xarqeo.mongodb.net/?retryWrites=true&w=majority");

        MongoDatabase db = client.getDatabase("MangaDB");
        System.out.println("Database connected !");
        MongoCollection col = db.getCollection("Mangas");

        Document sampleDoc = new Document().append("name", "Manga");

        col.insertOne(sampleDoc);

        List_All(col);

    }

    public static void List_All(MongoCollection collection){
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
        searchQuery.put("name" , "Manga");
        System.out.println("Retrieving specific Mongo Document");
        MongoCursor<Document> cursor = collection.find(searchQuery).iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }

}
