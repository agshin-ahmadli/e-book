package com.example.booknetwork.book;

import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private Integer Id;
    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String owner;
    private byte[] cover;
    private double rate;
    private boolean shareable;
    private boolean archived;
}
