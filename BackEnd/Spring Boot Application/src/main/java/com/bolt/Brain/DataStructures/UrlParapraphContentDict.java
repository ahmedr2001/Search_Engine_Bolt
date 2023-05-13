package com.bolt.Brain.DataStructures;

import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.WordsDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UrlParapraphContentDict {
    private final HashMap<Pair<Page,Integer>, List<UrlParagraphContent>> dictionary ;
    public UrlParapraphContentDict() {
        dictionary = new HashMap<>();
    }

    public void createDictionary(HashMap<Integer, List<WordsDocument>> resultsPerWord) {
        for(Integer wordIndex: resultsPerWord.keySet()) {               // 1. loop on index of wrd in phrase
            for (WordsDocument wDoc : resultsPerWord.get(wordIndex)) {  // 2. loop result of each on word
                for (Page pg : wDoc.getPages()) {                       // 3. loop on urls Content Details
                    List<Integer> paragraphIndex = pg.getParagraphIndexes();
                    List<Integer> wordIndexInP = pg.getWordIndexes();
                    for(int i = 0;i < paragraphIndex.size();i++) {      // 4. loop on details of each exist word in doc
                        Pair<Page, Integer> key = new Pair<>(pg, paragraphIndex.get(i));
                        List<UrlParagraphContent> val;
                        // if exist get it -> else create new List
                        if(dictionary.containsKey(key)) {
                            val = dictionary.get(key);
                        } else val = new ArrayList<>();
                        // add the content
                        val.add(new UrlParagraphContent(wordIndexInP.get(i),wordIndex, pg, i));
                        // add it to dict
                        dictionary.put(key, val);
                    }
                }

            }
        }
    }


    public void filterDictionary(int phraseWordsLength) {
        for(Pair<Page,Integer> urlParagraph : dictionary.keySet()) {
            List<UrlParagraphContent> contents = dictionary.get(urlParagraph);
            // sort it by wordIndexInParagraph
            UrlParagraphContent.UrlParagraphContentSorter.sortByWordIndex(contents);

            // loop on them and make it sure it's 0 .... 1 .... 2 ..etc.. n
            int shouldWordIndex = 0;
            for(UrlParagraphContent content: contents) {
                if(content.wordIndexPhrase == shouldWordIndex)
                    shouldWordIndex++;
                if(shouldWordIndex == phraseWordsLength) break; // success stop lopping
            }
            //fail should remove it
            if(shouldWordIndex != phraseWordsLength) {
                for(UrlParagraphContent content: contents) {
                    content.remove();
                }
            }

        }
    }

    void removeUrlParagraph(List<UrlParagraphContent> contents, Page url) {
        for(UrlParagraphContent content: contents) {
            content.remove();
        }
        if(url.getParagraphIndexes().size() == 0) {     // if url become empty remove it

        }
    }
}
