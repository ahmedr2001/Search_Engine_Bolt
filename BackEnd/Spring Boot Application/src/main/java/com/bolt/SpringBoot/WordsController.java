package com.bolt.SpringBoot;

import com.bolt.Brain.QueryProcessor.QueryProcessor;
import com.bolt.Brain.Ranker.MainRanker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

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
        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
//        return new ResponseEntity<List<WordsDocument>>(service.findWords(q), HttpStatus.OK);
    }
}
