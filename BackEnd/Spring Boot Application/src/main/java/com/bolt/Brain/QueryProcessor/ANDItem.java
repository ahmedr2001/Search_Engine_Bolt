package com.bolt.Brain.QueryProcessor;

import com.bolt.SpringBoot.WordsDocument;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class ANDItem extends BooleanItem {
    public ANDItem(ProcessQueryUnit processQueryUnit, String content) {
        super(processQueryUnit, content);
    }
    @Override
    public void executeOne(List<BooleanItem> items, int index) throws IOException {
        List<String> phraseWordsStemming1 = processQueryUnit.process(items.get(index - 1).getContent());
        List<String> phraseWords1 = processQueryUnit.basicProcess(items.get(index - 1).getContent());

        List<String> phraseWordsStemming2 = processQueryUnit.process(items.get(index + 1).getContent());
        List<String> phraseWords2 = processQueryUnit.basicProcess(items.get(index + 1).getContent());


        List<WordsDocument> basicRes = QueryProcessor.getWordsResult(phraseWordsStemming1);
        basicRes.addAll(QueryProcessor.getWordsResult(phraseWordsStemming2));

        results = QueryProcessor.runPhraseSearching(basicRes, getPattern(phraseWords1, phraseWords2));

        items.remove(index - 1);
        items.remove(index);
    }


    @Override
    public void executeSets(List<BooleanItem> items, int index) throws IOException {
        List<WordsDocument> res1 = items.get(index - 1).getResults();

        items.get(index+1).executeOne(items, index + 1);
        List<WordsDocument> res2 = items.get(index + 1).getResults();

        HashSet<Integer> set1 = getParagraphIndexes(res1);
        HashSet<Integer> set2 = getParagraphIndexes(res2);

        set1.retainAll(set2);    //add

        res1.addAll(res2);
        setResults(res1, set1);

        items.remove(index - 1);
        items.remove(index);
    }

    @Override
    public Pattern getPattern(List<String> phraseWords1, List<String> phraseWords2) {
        String regex = ".*" + String.join(".*", phraseWords1) + ".*" + String.join(".*", phraseWords2) + ".*";
        return  Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }


    static public boolean isAND(List<String> tokens, int index) {
        return (tokens.get(index).equalsIgnoreCase("and")&& PhraseItem.isPhrase(tokens, index-1)&& PhraseItem.isPhrase(tokens, index+1));
    }
}
