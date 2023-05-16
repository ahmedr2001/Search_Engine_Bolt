package com.bolt.SpringBoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SearchHistoryCollection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistoryDocument {
    @Id
    private ObjectId id;
    private String body;
    private Integer visited;
}
