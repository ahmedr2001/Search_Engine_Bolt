package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlerRepository extends MongoRepository<CrawlerDocument, ObjectId> {
    List<CrawlerDocument> findByURL(String url);
}
