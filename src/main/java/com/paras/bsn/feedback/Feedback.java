package com.paras.bsn.feedback;

import com.paras.bsn.book.Book;
import com.paras.bsn.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Feedback extends BaseEntity {

    private String comment;
    private Double note;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
