package com.example.booknetwork.feedback;

import lombok.*;
import org.hibernate.annotations.SecondaryRow;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {

    private Double note;
    private String comment;
    private boolean ownFeedback;
}
