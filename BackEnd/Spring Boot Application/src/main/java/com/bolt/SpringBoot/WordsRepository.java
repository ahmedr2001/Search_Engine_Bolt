package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordsRepository extends MongoRepository<WordsDocument, ObjectId> {
    List<WordsDocument> findByWord(String word);
}
