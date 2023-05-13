package com.bolt.SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParagraphService {

    @Autowired
    private ParagraphRepository repository;

    ParagraphService(){
        ;
    }

    public ParagraphDocument findParagraph(Integer id) {

        return repository.findParagraphById(id);
    }
}
