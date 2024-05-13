package com.example.booknetwork.book;

import com.example.booknetwork.common.BaseEntity;
import com.example.booknetwork.feedback.Feedback;
import com.example.booknetwork.history.BookTransactionHistory;
import com.example.booknetwork.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.UnknownServiceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Book extends BaseEntity {

    private String title;

    private String authorName;

    private String isbn;

    private String synopsis;

    private String bookCover;

    private boolean archived;

    private boolean shareable;


    @ManyToOne
    @JoinColumn(name = "owner_id") // this is optional
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory>histories = new ArrayList<>();

    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        double rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0)/10.0;
        return roundedRate;
    }

}
