package com.example.booknetwork.history;

import com.example.booknetwork.book.Book;
import com.example.booknetwork.common.BaseEntity;
import com.example.booknetwork.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookTransactionHistory extends BaseEntity {

    // user relationship
    @ManyToOne
    private User user;

    // book relationship
    @ManyToOne
    private Book book;

    private boolean returned;
    private boolean returnedApproved;


}
