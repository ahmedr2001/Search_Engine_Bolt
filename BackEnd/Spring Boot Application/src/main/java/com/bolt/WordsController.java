package com.bolt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/search")
public class WordsController {

    @Autowired
    private WordsService service;

    @GetMapping("/all")
    public ResponseEntity<List<WordsDocument>> allWords() {
        return new ResponseEntity<List<WordsDocument>>(service.allWords(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<WordsDocument>> search(@RequestParam String q) {
        return new ResponseEntity<List<WordsDocument>>(service.findWords(q), HttpStatus.OK);
    }
}
