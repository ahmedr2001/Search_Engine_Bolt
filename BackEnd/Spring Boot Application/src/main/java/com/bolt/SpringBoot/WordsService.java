package com.bolt.SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class WordsService {

    @Autowired
    private WordsRepository repository;

    WordsService(){
        ;
    }

    public List<WordsDocument> allWords() {
        return repository.findAll();
    }

    public List<WordsDocument> findWords(String q) {
        System.out.println(q);

        List<WordsDocument> finalResults = new ArrayList<>();

        finalResults = Stream.concat(repository.findByWord(q).stream(), finalResults.stream()).toList();

        String[] ar = q.split(" ");
        if (ar.length > 1) {
            for (String s : ar) {
                finalResults = Stream.concat(repository.findByWord(s).stream(), finalResults.stream()).toList();
            }
        }

        return finalResults;
    }
}
