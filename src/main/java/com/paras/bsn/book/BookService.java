package com.paras.bsn.book;

import com.paras.bsn.common.PageResponse;
import com.paras.bsn.exception.OperationNotPermittedException;
import com.paras.bsn.file.FileStorageService;
import com.paras.bsn.history.BookTransactionHistory;
import com.paras.bsn.history.BookTransactionHistoryRepository;
import com.paras.bsn.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.paras.bsn.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Book saveBook(BookRequest bookRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book);
    }

    public BookResponse getBookById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
    }

    public PageResponse<BookResponse> getAllBooks(Integer page, Integer size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllByDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> getBooksByOwner(Integer page, Integer size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> getAllBorrowedBooks(Integer page, Integer size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> borrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = borrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> getAllReturnedBooks(Integer page, Integer size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> borrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = borrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        book.setShareable(!book.getShareable());
        bookRepository.save(book);
        return id;
    }

    public Integer updateArchiveStatus(Integer id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        book.setArchived(!book.getArchived());
        bookRepository.save(book);
        return id;
    }

    public Integer borrowBook(Integer id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (book.getArchived() || !book.getShareable()) {
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are the owner of this book");
        }
        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(book.getId(), user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (book.getArchived() || !book.getShareable()) {
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are the owner of this book");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(id, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You have not borrowed this book"));
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnedBorrowedBook(Integer id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (book.getArchived() || !book.getShareable()) {
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet"));
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCover(Integer id, MultipartFile file, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found for ID: " + id));
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not the owner of this book");
        }
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
    }
}
