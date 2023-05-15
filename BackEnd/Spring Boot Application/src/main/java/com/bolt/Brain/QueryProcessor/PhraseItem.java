package com.bolt.Brain.QueryProcessor;

import com.bolt.SpringBoot.WordsDocument;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


public class PhraseItem extends BooleanItem {

    public PhraseItem(ProcessQueryUnit processQueryUnit, String content) {
        super(processQueryUnit, content);
    }
    @Override
    public void execute(List<BooleanItem> items, int index) throws IOException {
        List<String> phraseWordsStemming = processQueryUnit.process(content);
        List<String> phraseWords = processQueryUnit.basicProcess(content);

        Pattern phrasePattern = getPattern(phraseWords);
        //get phrase results as normal
        results = QueryProcessor.getWordsResult(phraseWordsStemming);
        results = QueryProcessor.runPhraseSearching(results, phrasePattern);
    }

    @Override
    public Pattern getPattern(List<String> words) {
        String regex = ".*" + String.join(".*", words) + ".*";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }


    static public boolean isPhrase(List<String> tokens, int index) {
        String ph = tokens.get(index);
        return (ph.startsWith("\"") && ph.endsWith("\""));
    }
}
