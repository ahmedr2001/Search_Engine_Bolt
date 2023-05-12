package com.bolt.SpringBoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    @Id
    private int id;
    private Double TF;
    private List<String> tagTypes;
    private List<String> tagIndexes;
    private List<String> paragraphIndexes;
    private List<String> wordIndexes;
}
