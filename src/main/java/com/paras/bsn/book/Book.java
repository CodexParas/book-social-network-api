package com.paras.bsn.book;

import com.paras.bsn.common.BaseEntity;
import com.paras.bsn.feedback.Feedback;
import com.paras.bsn.history.BookTransactionHistory;
import com.paras.bsn.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    private Boolean archived;
    private Boolean shareable;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @Transient
    public Double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }
}
