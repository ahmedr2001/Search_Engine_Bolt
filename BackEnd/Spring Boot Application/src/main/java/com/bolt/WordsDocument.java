package com.bolt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "WordsCollection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordsDocument {
    @Id
    private ObjectId id;
    private String word;
    private Double IDF;
    private List<Page> pages;
}
