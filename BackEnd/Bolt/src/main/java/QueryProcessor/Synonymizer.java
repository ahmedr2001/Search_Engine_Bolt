package QueryProcessor;


import Indexer.Stemmer;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Synonymizer {

    public static void main(String args[]) throws IOException {
        List<String> words = new ArrayList<>();
        words.add("running");
        words.add("synonym");

        Synonymizer synonymizer = new Synonymizer();
        List<String> newArr =  synonymizer.runSynonymizer(words);
        System.out.println(newArr);

        Stemmer stemmer = new Stemmer();
        List<String> newArrS  = stemmer.runStemmer(words);
        System.out.println(newArrS);
    }

    //get words after processing [lowerCase - remove stop words ] => [synonums + stemmaing]
    public List<String> runSynonymizer(List<String> words) throws IOException {
        List<String> synonymWords = new ArrayList<>();
        // ======= Initializers

        // 1. the WordNet dictionary
        String path = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
        URL url = new URL("file", null, path);
        IDictionary dict = new edu.mit.jwi.Dictionary(url);
        dict.open();

        // 2. Initialize the WordNet stemmer
        WordnetStemmer stemmer = new WordnetStemmer(dict);




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
        }

        return synonymWords;
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
