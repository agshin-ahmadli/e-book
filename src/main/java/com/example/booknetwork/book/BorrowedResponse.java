package com.example.booknetwork.book;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedResponse {

    private Integer Id;
    private String title;
    private String author;
    private String isbn;
    private double rate;
    private boolean returned;
    private boolean returnedApproved;
}
