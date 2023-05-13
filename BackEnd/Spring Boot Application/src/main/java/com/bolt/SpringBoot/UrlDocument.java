package com.bolt.SpringBoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "URLsCollection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDocument {
    @Id
    private int _id;
    private String url;
    private String title;
    private Double page_rank;
}
