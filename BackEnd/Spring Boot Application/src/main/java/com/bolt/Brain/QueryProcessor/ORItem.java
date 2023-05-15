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
        if(items.get(index - 1).getResults() == null) items.get(index-1).executeOne(items, index-1);
        if(items.get(index + 1).getResults() == null) items.get(index+1).executeOne(items, index+1);


        results = items.get(index - 1).getResults();
        results.addAll(items.get(index - 1).getResults());

        items.remove(index - 1);
        items.remove(index);
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
