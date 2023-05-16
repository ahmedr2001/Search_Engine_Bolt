package com.bolt.Brain.QueryProcessor;

import com.bolt.SpringBoot.WordsDocument;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class ORItem extends BooleanItem{

    public ORItem(ProcessQueryUnit processQueryUnit, String content) {
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
    public Pattern getPattern(List<String> phraseWords1, List<String> phraseWords2) {
        String phraseWords1Regex = String.join(".*", phraseWords1);
        String phraseWords2Regex = String.join(".*", phraseWords2);
        ;
        return  Pattern.compile("^(?=.*" + phraseWords1Regex + ")(|.*" + phraseWords2Regex + ").*$", Pattern.CASE_INSENSITIVE);
    }



    public void executeSets(List<BooleanItem> items, int index) throws IOException {
        results = items.get(index - 1).getResults();

        items.get(index+1).executeOne(items, index + 1);
        results.addAll( items.get(index + 1).getResults() );

        items.remove(index - 1);
        items.remove(index);

    }
    static public boolean isOR(List<String> tokens, int index) {
        return (
                tokens.get(index).equalsIgnoreCase("or")
                        && PhraseItem.isPhrase(tokens, index-1)
                        && PhraseItem.isPhrase(tokens, index+1));
    }
}
