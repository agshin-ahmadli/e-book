package com.example.booknetwork.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        Integer id,
        @NotNull(message = "101")
        @NotEmpty(message = "101")
        String title,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String authorName,
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String isbn,
        @NotNull(message = "104")
        @NotEmpty(message = "104")
        String synopsis,
        boolean shareable
) {
}
