package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParagraphRepository extends MongoRepository<ParagraphDocument, ObjectId> {
    Optional<ParagraphDocument> findById(ObjectId id);


}
