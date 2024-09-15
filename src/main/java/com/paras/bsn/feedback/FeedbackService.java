package com.paras.bsn.feedback;

import com.paras.bsn.book.Book;
import com.paras.bsn.book.BookRepository;
import com.paras.bsn.common.PageResponse;
import com.paras.bsn.exception.OperationNotPermittedException;
import com.paras.bsn.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer createFeedback(FeedbackRequest feedbackRequest, Authentication authentication) {
        Book book = bookRepository.findById(feedbackRequest.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with id " + feedbackRequest.bookId()));
        if (book.getArchived() || !book.getShareable()) {
            throw new OperationNotPermittedException("Book is not available for feedback");
        }
        User user = (User) authentication.getPrincipal();
        if (user.getId().equals(book.getOwner().getId())) {
            throw new OperationNotPermittedException("Owner can't give feedback");
        }
        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);
        return feedbackRepository.save(feedback).getId();
    }


    public PageResponse<FeedbackResponse> getFeedbackByBookId(Integer bookId, Integer page, Integer size, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) authentication.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
