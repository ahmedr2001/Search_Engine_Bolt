package com.bolt.SpringBoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CrawledPages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerDocument {
    @Id
    private Integer _id;
    private String URL;
    private String KEY;
    private String BODY;
    private String TITLE;
}
