package com.isec.das.project2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Operation {
    private String id;
    private boolean done;
    private Object result;
    private Map<String, Object> metadata;
}
