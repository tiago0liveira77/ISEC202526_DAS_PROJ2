package com.isec.das.project2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    private String id;
    private String bookId;
    private String personId;
    private String libraryId;
    private String loanDate;
    private boolean active;
}

