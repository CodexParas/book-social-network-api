package com.paras.bsn.feedback;

import com.paras.bsn.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedbacks")
@Tag(name = "Feedback", description = "APIs for Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> createFeedback(
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            Authentication authentication
    ) {
        return ResponseEntity.ok(feedbackService.createFeedback(feedbackRequest, authentication));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> getFeedbackByBookId(
            @PathVariable Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbackByBookId(bookId, page, size, authentication));
    }

}
