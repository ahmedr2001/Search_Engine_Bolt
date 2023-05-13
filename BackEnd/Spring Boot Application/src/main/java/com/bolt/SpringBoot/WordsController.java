package com.bolt.SpringBoot;

import com.bolt.Brain.QueryProcessor.QueryProcessor;
import com.bolt.Brain.Ranker.MainRanker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://127.0.0.1:5173")
@RestController
@RequestMapping("/search")
public class WordsController {

    @Autowired
    private WordsService wordsService;
    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private UrlsService urlsService;

    @Autowired
    private ParagraphService paragraphService;

    @GetMapping("/all")
    public ResponseEntity<List<WordsDocument>> allWords() {
        return new ResponseEntity<List<WordsDocument>>(wordsService.allWords(), HttpStatus.OK);
    }

    @GetMapping

    public ResponseEntity<List<String>> search(@RequestParam String q) throws IOException {
        QueryProcessor queryProcessor = new QueryProcessor(crawlerService, wordsService, paragraphService);
        List<WordsDocument> RelatedDocuments = queryProcessor.run(q);
        MainRanker mainRanker = new MainRanker(RelatedDocuments, urlsService );
        List<String>list = mainRanker.runRanker();
        list = list.stream().distinct()                               //4.Remove Duplicates
                .collect(Collectors.toList());

        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
//        return new ResponseEntity<List<WordsDocument>>(service.findWords(q), HttpStatus.OK);
    }
}
