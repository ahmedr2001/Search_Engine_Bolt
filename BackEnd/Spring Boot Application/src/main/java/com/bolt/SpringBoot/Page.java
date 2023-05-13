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
    private int urlId;
    private Double TF;
    private List<String> tagTypes;
    private List<Integer> tagIndexes;
    private List<Integer> paragraphIndexes;
    private List<Integer> wordIndexes;
}
