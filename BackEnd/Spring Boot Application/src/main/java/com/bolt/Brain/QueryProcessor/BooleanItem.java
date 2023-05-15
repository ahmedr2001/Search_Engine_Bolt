package com.bolt.Brain.QueryProcessor;

import com.bolt.SpringBoot.Page;
import com.bolt.SpringBoot.WordsDocument;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.List;
public abstract  class BooleanItem {
    ProcessQueryUnit processQueryUnit;
    String content;

    List<WordsDocument> results = null;
    public BooleanItem(ProcessQueryUnit processQueryUnit, String content) {
        this.processQueryUnit = processQueryUnit;
        this.content = content;
    }
    public Pattern getPattern(List<String> words) {
        return  null;
    };
    public Pattern getPattern(List<String> words1,List<String> words2) {
        return null;
    }

    public void setContent(String c) {
        content = c;
    }

    public String getContent(){
        return content;
    }

    public List<WordsDocument> getResults() {
        return results;
    }

    public HashSet<Integer> getParagraphIndexes(List<WordsDocument> wordDocs) {
        HashSet<Integer> res = new HashSet<> ();
        for(WordsDocument wDoc: wordDocs) {
            for(Page url: wDoc.getPages()){
                for(Integer pid : url.getParagraphIndexes())
                    res.add(pid);
            }
        }
        return res;
    }
    public void setResults(List<WordsDocument> words, HashSet<Integer> st) {
        Iterator<WordsDocument> iteratorWDoc = words.iterator();
        while (iteratorWDoc.hasNext()) {
            WordsDocument wDoc = iteratorWDoc.next();
            Iterator<Page> iteratorPages = wDoc.getPages().iterator();
            while (iteratorPages.hasNext()) {
                Page pg = iteratorPages.next();
                List<Integer> paragraphIndexes = pg.getParagraphIndexes();
                for (int i = 0; i < paragraphIndexes.size(); i++) {           //3. loop on p in url
                    Integer paragraphId = paragraphIndexes.get(i);

                    if (!st.contains(paragraphId)) {           // check pattern matcher
                        QueryProcessor.removeParagraphData(pg, i);
                        i--; // to calibrate loop

                    }
                }
                if (pg.getParagraphIndexes().isEmpty()) iteratorPages.remove();
            }
            if (wDoc.getPages().isEmpty()) iteratorWDoc.remove();
        }
        results = words;
    }



    public void execute(List<BooleanItem> items, int index) throws IOException {
        if(items.get(index) instanceof PhraseItem || items.get(index) instanceof WordItem )
            this.executeOne(items, index);
        else if (items.get(index - 1) instanceof PhraseItem && items.get(index + 1) instanceof PhraseItem)
            this.executeOne(items, index);
        else this.executeSets(items,index);
    }
    abstract public void executeOne(List<BooleanItem> items, int index) throws IOException;

    public void executeSets(List<BooleanItem> items, int index) throws IOException {

    }



}
