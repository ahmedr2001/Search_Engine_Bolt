package com.bolt.SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlerService {

    @Autowired
    private CrawlerRepository repository;

    public String getUrlBody(String url) {
        String result = null;
        List<CrawlerDocument> list = repository.findByURL(url);
        if (list.size() > 0) {
            result = list.get(0).getBODY();
        }
        return result;
    }
    public List<CrawlerDocument> getCrawledPages(){
        return repository.findAll() ;
    }
}
