package com.bolt;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordsRepository extends MongoRepository<WordsDocument, ObjectId> {
    List<WordsDocument> findByWord(String word);
}
