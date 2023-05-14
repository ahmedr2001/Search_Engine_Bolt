package com.bolt.SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParagraphService {

    @Autowired
    private ParagraphRepository repository;

    ParagraphService() {
        ;
    }

    public ParagraphDocument findParagraph(Integer id) {

        return repository.findParagraphById(id);
    }

    public List<String> getParagraphs(String[] pids) {
        List<String> result = new ArrayList<>();
        for (String pid : pids) {
            result.add(repository.findParagraphById(Integer.parseInt(pid)).getParagraph());
        }
        return result;
    }
}
