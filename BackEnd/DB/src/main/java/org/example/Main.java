package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MongoClient client = MongoClients.create("mongodb+srv://ahmedr2001:eng3469635@javasearchengine.8xarqeo.mongodb.net/?retryWrites=true&w=majority");

        MongoDatabase db = client.getDatabase("MangaDB");

        MongoCollection col = db.getCollection("Mangas");

        Document sampleDoc = new Document().append("name", "Manga");

        col.insertOne(sampleDoc);
    }


}