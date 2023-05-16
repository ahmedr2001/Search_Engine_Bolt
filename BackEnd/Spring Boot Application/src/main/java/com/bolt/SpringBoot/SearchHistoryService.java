package com.bolt.SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SearchHistoryService {

    @Autowired
    private SearchHistoryRespository repository;

    SearchHistoryService(){
        ;
    }

    public List<SearchHistoryDocument> allResults() {
        return repository.findAll();
    }

    public void increaseCount(String body) {
        repository.increaseByBody(body);
    }

}
