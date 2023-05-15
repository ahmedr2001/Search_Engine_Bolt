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
    abstract public void execute(List<BooleanItem> items, int index) throws IOException;
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



    public void executeSets(List<BooleanItem> items, int index) throws IOException {
       List<WordsDocument> res1 = items.get(index - 1).getResults();

       items.get(index+1).execute(items, index + 1);
       List<WordsDocument> res2 = items.get(index + 1).getResults();

       HashSet<Integer> set1 = getParagraphIndexes(res1);
       HashSet<Integer> set2 = getParagraphIndexes(res2);

       set1.retainAll(set2);    //add

        res1.addAll(res2);
        setResults(res1, set1);


    }



}
