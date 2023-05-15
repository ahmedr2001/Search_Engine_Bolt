package com.bolt.Brain.QueryProcessor;

import com.bolt.SpringBoot.WordsDocument;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class NOTItem extends BooleanItem{

    public NOTItem(ProcessQueryUnit processQueryUnit, String content) {
        super(processQueryUnit, content);
    }
    @Override
    public void execute(List<BooleanItem> items, int index) throws IOException {
        List<String> phraseWordsStemming1 = processQueryUnit.process(items.get(index - 1).getContent());
        List<String> phraseWords1 = processQueryUnit.basicProcess(items.get(index - 1).getContent());

        List<String> phraseWords2 = processQueryUnit.basicProcess(items.get(index + 1).getContent());


        List<WordsDocument> basicRes = QueryProcessor.getWordsResult(phraseWordsStemming1);
        results = QueryProcessor.runPhraseSearching(basicRes, getPattern(phraseWords1, phraseWords2));
    }

    @Override
    public Pattern getPattern(List<String> phraseWords1, List<String> phraseWords2) {
        String phraseWords1Regex = String.join(".*", phraseWords1);
        String phraseWords2Regex = String.join(".*", phraseWords2);
        ;
        return  Pattern.compile("^(?=.*" + phraseWords1Regex + ")(?!.*" + phraseWords2Regex + ").*$", Pattern.CASE_INSENSITIVE);
    }

    public void executeSets(List<BooleanItem> items, int index) throws IOException {
        List<WordsDocument> res1 = items.get(index - 1).getResults();

        items.get(index+1).execute(items, index + 1);
        List<WordsDocument> res2 = items.get(index + 1).getResults();

        HashSet<Integer> set1 = getParagraphIndexes(res1);
        HashSet<Integer> set2 = getParagraphIndexes(res2);

        set1.removeAll(set2);    //add

        res1.addAll(res2);
        setResults(res1, set1);

    }
    static public boolean isNOT(List<String> tokens, int index) {
        return (
                tokens.get(index).equalsIgnoreCase("not")
                        && PhraseItem.isPhrase(tokens, index-1)
                        && PhraseItem.isPhrase(tokens, index+1));
    }
}
