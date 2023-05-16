package com.bolt.SpringBoot;

import com.bolt.Brain.QueryProcessor.QueryProcessor;
import com.bolt.Brain.Ranker.MainRanker;
import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.StringDecoder;
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
    @Autowired
    private SearchHistoryService searchHistoryService;

    @GetMapping("/all")
    public ResponseEntity<List<WordsDocument>> allWords() {
        return new ResponseEntity<List<WordsDocument>>(wordsService.allWords(), HttpStatus.OK);
    }

    @GetMapping("/p")
    public ResponseEntity<List<String>> getParagraph(@RequestParam String[] pids) {
        return new ResponseEntity<List<String>>(paragraphService.getParagraphs(pids), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<Document>> search(@RequestParam String q) throws IOException {
        searchHistoryService.increaseCount(q);
        QueryProcessor queryProcessor = new QueryProcessor(crawlerService, wordsService, paragraphService);
        List<WordsDocument> RelatedDocuments = queryProcessor.run(q);
        List<String> originalWords = queryProcessor.getOriginalWords();
        MainRanker mainRanker = new MainRanker(RelatedDocuments, urlsService , originalWords );
        List<Document>list = mainRanker.runRanker();
        return new ResponseEntity<List<Document>>(list, HttpStatus.OK);
//        return new ResponseEntity<List<WordsDocument>>(service.findWords(q), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SearchHistoryDocument>> getHistory() {
        return new ResponseEntity<List<SearchHistoryDocument>>(searchHistoryService.allResults(), HttpStatus.OK);
    }

}