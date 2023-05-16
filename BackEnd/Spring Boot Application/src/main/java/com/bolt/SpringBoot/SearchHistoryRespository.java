package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SearchHistoryRespository extends MongoRepository<SearchHistoryDocument, ObjectId> {
    List<SearchHistoryDocument> findAll();

    default SearchHistoryDocument increaseByBody(String body) {
        SearchHistoryDocument document = findOneByBody(body);
        if (document == null) {
            document = new SearchHistoryDocument();
            document.setBody(body);
            document.setVisited(1);
            save(document);
        } else {
            document.setVisited(document.getVisited() + 1);
            save(document);
        }
        return document;
    }

    SearchHistoryDocument findOneByBody(String body);


}
