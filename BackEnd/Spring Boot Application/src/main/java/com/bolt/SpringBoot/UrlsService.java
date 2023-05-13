package com.bolt.SpringBoot;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UrlsService {

    @Autowired
    private UrlRepository repository;

    UrlsService(){
        ;
    }

    public List<UrlDocument> allUrls() {
        return repository.findAll();
    }


    public String findUrl(int id) {
        return repository.findUrlById(id);
    }

    public Double findRank(String url) {
        return repository.findByUrl(url).getRank();
    }
}
