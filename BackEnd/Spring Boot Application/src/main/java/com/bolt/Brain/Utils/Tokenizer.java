package com.bolt.Brain.Utils;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    public  List<String> runTokenizer(String sentence){
        sentence = sentence.toLowerCase();
        List<String> words =new <String>Vector();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher match = pattern.matcher(sentence);
        while (match.find()) {
            words.add(match.group());
        }
        return words;
    }

}
