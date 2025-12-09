package com.isec.das.project2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class BookOverview {
    // "Defaults to no text" conforme requisito [cite: 1605]
    private String text = "";
}
