package com.example.booknetwork.common;

import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int number;
    private int size;
    private long totalElement;
    private int totalPages;
    private boolean first;
    private boolean last;
}
