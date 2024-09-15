package com.paras.bsn.book;

import com.paras.bsn.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> saveBook(
            @Valid @RequestBody BookRequest bookRequest,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.saveBook(bookRequest, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, authentication));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.getBooksByOwner(page, size, authentication));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.getAllBorrowedBooks(page, size, authentication));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.getAllReturnedBooks(page, size, authentication));
    }

    @PatchMapping("/shareable/{id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(id, authentication));
    }

    @PatchMapping("/archived/{id}")
    public ResponseEntity<Integer> updateArchiveStatus(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.updateArchiveStatus(id, authentication));
    }

    @PostMapping("/borrow/{id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.borrowBook(id, authentication));
    }

    @PatchMapping("/borrow/return/{id}")
    public ResponseEntity<Integer> returnBorrowedBook(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.returnBorrowedBook(id, authentication));
    }

    @PatchMapping("/borrow/return/approve/{id}")
    public ResponseEntity<Integer> approveReturnedBorrowedBook(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.approveReturnedBorrowedBook(id, authentication));
    }

    @PostMapping(value = "/cover/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBookCover(
            @PathVariable Integer id,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        bookService.uploadBookCover(id, file, authentication);
        return ResponseEntity.accepted().build();
    }
}
