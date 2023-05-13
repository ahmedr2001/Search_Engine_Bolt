package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlDocument, Integer> {
    Optional<UrlDocument> findById(int id);

    String findUrlById(int id);

    UrlDocument findByUrl(String url);

}
