package com.bolt.Brain.DataStructures;

import com.bolt.SpringBoot.Page;

import java.util.Comparator;
import java.util.List;

public  class UrlParagraphContent {
    public Integer wordIndex;
    public Integer wordIndexPhrase;

    Page refPage;

    Integer paragraphIndexArr; //

    public UrlParagraphContent(Integer wordIndex, Integer wordIndexPhrase, Page refPage, Integer paragraphIndexArr) {
        this.wordIndex = wordIndex;
        this.wordIndexPhrase = wordIndexPhrase;
        this.refPage = refPage;
        this.paragraphIndexArr = paragraphIndexArr;
    }

    public  void remove() {
        int indexToRemove = paragraphIndexArr;
        refPage.getParagraphIndexes().remove(indexToRemove);
        refPage.getWordIndexes().remove(indexToRemove);
        refPage.getTagIndexes().remove(indexToRemove);
        refPage.getTagTypes().remove(indexToRemove);
    }

    public static  class UrlParagraphContentSorter {

        public static void sortByWordIndex(List<UrlParagraphContent> list) {
            Comparator<UrlParagraphContent> comparator = Comparator.comparingInt(o -> o.wordIndex);
            list.sort(comparator);
        }

    }
}