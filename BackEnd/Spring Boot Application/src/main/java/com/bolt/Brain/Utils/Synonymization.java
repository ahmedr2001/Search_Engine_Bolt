<<<<<<< HEAD
package com.bolt.Brain.Utils;


import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Synonymization {
    public IDictionary dict;
    public WordnetStemmer stemmer;

    public Synonymization() throws IOException {
        String absolutePath = "/home/ahmedosamahelmy/IdeaProjects/Search_Engine_Bolt/BackEnd/Spring Boot Application/";
//        String absolutePath ="./";
        String path = absolutePath+"dict";
        URL url = new URL("file", null, path);
        this.dict = new edu.mit.jwi.Dictionary(url);
        dict.open();

        stemmer =  new WordnetStemmer(dict);
    }
    public static void main(String args[])throws IOException  {
        Synonymization synonymization = new Synonymization();
        List<String> words = new ArrayList<>();
        words.add("java");
        words.add("synonym");

        //==== test stemmer
        List<String> stems = synonymization.getStemsOfWords(words);
        List<String> syn = synonymization.runSynonymization(words);
        List<String> stemsSyn = synonymization.getStemsOfWords(syn);

        System.out.println(words);
        System.out.println(stems);

        System.out.println(syn);
        System.out.println(stemsSyn);
    }

    //get words after processing [lowerCase - remove stop words ] => [synonums + stemmaing]
    public List<String> runSynonymization(List<String> words) throws IOException {
        List<String> synonymWords = new ArrayList<>();

        for (String word : words) { // Loop through the input words and find synonyms for each one

            // Find the part of speech for the word [adjective or noun or etc... ]
            POS pos = getPartOfSpeech(word, dict);
            if (pos != null) {

                List<String> stems = stemmer.findStems(word, pos); // Find the stems of the word (if any)
                Set<String> synonyms = new HashSet<>();

                for (String stem : stems) { // Loop through each stem and find its synonyms
                    IIndexWord indexWord = dict.getIndexWord(stem, pos);    //get index
                    if (indexWord != null) {
                        for (IWordID wordID : indexWord.getWordIDs()) {
                            ISynset synset = dict.getWord(wordID).getSynset();
                            for (IWord ww : synset.getWords()) {
                                String lemma = ww.getLemma().replace("_", " ");
                                synonyms.add(lemma);
                            }
                        }
                    }
                }


                // Add the original word and its synonyms to the output list
                synonymWords.add(word);
                synonymWords.addAll(synonyms);
            }
            else {
                synonymWords.add(word);
            }
        }

        return getStemsOfWords(synonymWords);
    }

    private List<String> getStemsOfWord(String word) {
        POS pos = getPartOfSpeech(word, dict);
        if(pos == null)
            return null;
        List<String> stems = stemmer.findStems(word, pos); // Find the stems of the word (if any)
        stems.add(word);
        return stems;
    }
    public List<String> getStemsOfWords(List<String> words) {
        List<String> stemmedWords = new ArrayList<String>();
        for(String word: words) {
            List<String> stem = getStemsOfWord(word);
            if(stem != null)
                stemmedWords.addAll(stem);
            else stemmedWords.add(word);
        }
        return stemmedWords;
    }


    private static POS getPartOfSpeech(String word, IDictionary dict) {
        // Find the part of speech for the word
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        List<String> stems = stemmer.findStems(word, null);
        if (!stems.isEmpty()) {
            String stem = stems.get(0);
            if (dict.getIndexWord(stem, POS.NOUN) != null) {
                return POS.NOUN;
            } else if (dict.getIndexWord(stem, POS.VERB) != null) {
                return POS.VERB;
            } else if (dict.getIndexWord(stem, POS.ADJECTIVE) != null) {
                return POS.ADJECTIVE;
            } else if (dict.getIndexWord(stem, POS.ADVERB) != null) {
                return POS.ADVERB;
            }
        }
        return null;
    }
}
=======
///*
//package com.bolt.Brain.Utils;
//
//
//import edu.mit.jwi.IDictionary;
//import edu.mit.jwi.item.*;
//import edu.mit.jwi.morph.WordnetStemmer;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//public class Synonymization {
//    public IDictionary dict;
//    public WordnetStemmer stemmer;
//
//    public Synonymization() throws IOException {
////        String absolutePath = "/home/ahmedosamahelmy/IdeaProjects/Search_Engine_Bolt/BackEnd/Spring Boot Application/";
//        String absolutePath ="./";
//        String path = absolutePath+"dict";
//        URL url = new URL("file", null, path);
//        this.dict = new edu.mit.jwi.Dictionary(url);
//        dict.open();
//
//        stemmer =  new WordnetStemmer(dict);
//    }
//    public static void main(String args[])throws IOException  {
//        Synonymization synonymization = new Synonymization();
//        List<String> words = new ArrayList<>();
//        words.add("java");
//        words.add("synonym");
//
//        //==== test stemmer
//        List<String> stems = synonymization.getStemsOfWords(words);
//        List<String> syn = synonymization.runSynonymization(words);
//        List<String> stemsSyn = synonymization.getStemsOfWords(syn);
//
//        System.out.println(words);
//        System.out.println(stems);
//
//        System.out.println(syn);
//        System.out.println(stemsSyn);
//    }
//
//    //get words after processing [lowerCase - remove stop words ] => [synonums + stemmaing]
//    public List<String> runSynonymization(List<String> words) throws IOException {
//        List<String> synonymWords = new ArrayList<>();
//
//        for (String word : words) { // Loop through the input words and find synonyms for each one
//
//            // Find the part of speech for the word [adjective or noun or etc... ]
//            POS pos = getPartOfSpeech(word, dict);
//            if (pos != null) {
//
//                List<String> stems = stemmer.findStems(word, pos); // Find the stems of the word (if any)
//                Set<String> synonyms = new HashSet<>();
//
//                for (String stem : stems) { // Loop through each stem and find its synonyms
//                    IIndexWord indexWord = dict.getIndexWord(stem, pos);    //get index
//                    if (indexWord != null) {
//                        for (IWordID wordID : indexWord.getWordIDs()) {
//                            ISynset synset = dict.getWord(wordID).getSynset();
//                            for (IWord ww : synset.getWords()) {
//                                String lemma = ww.getLemma().replace("_", " ");
//                                synonyms.add(lemma);
//                            }
//                        }
//                    }
//                }
//
//
//                // Add the original word and its synonyms to the output list
//                synonymWords.add(word);
//                synonymWords.addAll(synonyms);
//            }
//            else {
//                synonymWords.add(word);
//            }
//        }
//
//        return getStemsOfWords(synonymWords);
//    }
//
//    private List<String> getStemsOfWord(String word) {
//        POS pos = getPartOfSpeech(word, dict);
//        if(pos == null)
//            return null;
//        List<String> stems = stemmer.findStems(word, pos); // Find the stems of the word (if any)
//        stems.add(word);
//        return stems;
//    }
//    public List<String> getStemsOfWords(List<String> words) {
//        List<String> stemmedWords = new ArrayList<String>();
//        for(String word: words) {
//            List<String> stem = getStemsOfWord(word);
//            if(stem != null)
//                stemmedWords.addAll(stem);
//            else stemmedWords.add(word);
//        }
//        return stemmedWords;
//    }
//
//
//    private static POS getPartOfSpeech(String word, IDictionary dict) {
//        // Find the part of speech for the word
//        WordnetStemmer stemmer = new WordnetStemmer(dict);
//        List<String> stems = stemmer.findStems(word, null);
//        if (!stems.isEmpty()) {
//            String stem = stems.get(0);
//            if (dict.getIndexWord(stem, POS.NOUN) != null) {
//                return POS.NOUN;
//            } else if (dict.getIndexWord(stem, POS.VERB) != null) {
//                return POS.VERB;
//            } else if (dict.getIndexWord(stem, POS.ADJECTIVE) != null) {
//                return POS.ADJECTIVE;
//            } else if (dict.getIndexWord(stem, POS.ADVERB) != null) {
//                return POS.ADVERB;
//            }
//        }
//        return null;
//    }
//}
//*/
>>>>>>> ee84ca76dc319291667c83e84582ef060c7b7871
