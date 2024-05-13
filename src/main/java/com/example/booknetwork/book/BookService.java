package com.example.booknetwork.book;

import com.example.booknetwork.common.PageResponse;
import com.example.booknetwork.exception.OperationNotPermittedException;
import com.example.booknetwork.file.FileStorageService;
import com.example.booknetwork.history.BookTransactionHistory;
import com.example.booknetwork.history.BookTransactionHistoryRepository;
import com.example.booknetwork.user.User;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final BookTransactionHistoryRepository bookTransactionHistory;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
return bookRepository.findById(bookId)
        .map(bookMapper::toBookResponse)
        .orElseThrow(()-> new EntityNotFoundException("No book found with the Id " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponse
                ,books.getNumber()
                ,books.getSize()
                ,books.getTotalElements()
                ,books.getTotalPages()
                ,books.isFirst()
                ,books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponse
                ,books.getNumber()
                ,books.getSize()
                ,books.getTotalElements()
                ,books.getTotalPages()
                ,books.isFirst()
                ,books.isLast()
        );
    }

    public PageResponse<BorrowedResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
    Page <BookTransactionHistory> allBorrowedBooks = bookTransactionHistory.findAllBorrowedBooks(pageable,user.getId());
    List<BorrowedResponse> borrowedResponses = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();
    return new PageResponse<>(
            borrowedResponses
            ,allBorrowedBooks.getNumber()
            ,allBorrowedBooks.getSize()
            ,allBorrowedBooks.getTotalElements()
            ,allBorrowedBooks.getTotalPages()
            ,allBorrowedBooks.isFirst()
            ,allBorrowedBooks.isLast()
    );

    }

    public PageResponse<BorrowedResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User)connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<BookTransactionHistory>allBorrowedBooks = bookTransactionHistory.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedResponse>bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                bookResponse
                ,allBorrowedBooks.getNumber()
                ,allBorrowedBooks.getSize()
                ,allBorrowedBooks.getTotalElements()
                ,allBorrowedBooks.getTotalPages()
                ,allBorrowedBooks.isFirst()
                ,allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with Id " + bookId));
        User user = ((User)connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(), user.getId())){////////////////*******
            throw new OperationNotPermittedException("You can not update books shareable status");
    }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
}

    public Integer updateUpdateArchiveStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new OperationNotPermittedException("No book found with Id  + bookId"));
        User user = ((User)connectedUser.getPrincipal());
        if(!Objects.equals(book.getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with Id " + bookId));
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book can not be borrowed since it is archived or not shareable");
        }
            User user = ((User)connectedUser.getPrincipal());
            if(!Objects.equals(book.getId(),user.getId())){
                throw new OperationNotPermittedException("You can not borrow your own book");
            }
            final boolean isAlreadyBorrowed = bookTransactionHistory.isAlreadyBorrowedByUser(bookId,user.getId());
            if(isAlreadyBorrowed){
                throw new OperationNotPermittedException("The requested book is already borrowed");
            }
            BookTransactionHistory bookTransactionHistory1 = BookTransactionHistory.builder()
                    .user(user)
                    .book(book)
                    .returned(false)
                    .returnedApproved(false)
                    .build();
            return bookTransactionHistory.save(bookTransactionHistory1).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with Id " + bookId));
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The book you request can not be borrowed");
        }
            User user = ((User)connectedUser.getPrincipal());
            if(Objects.equals(book.getId(),user.getId())){
                throw new OperationNotPermittedException("You can not borrow your own book");
            }
            BookTransactionHistory bookTransactionHistory1 = bookTransactionHistory.findByBookIdAndUserId(bookId,user)
                    .orElseThrow(()->new OperationNotPermittedException("You did not borrow this book"));

            bookTransactionHistory1.setReturned(true);
            return bookTransactionHistory.save(bookTransactionHistory1).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with Id " + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The book you request can not be borrowed");
        }
        User user = (User) connectedUser.getPrincipal();
        if(Objects.equals(book.getId(),user.getId())){
            throw new OperationNotPermittedException("You can not borrow your own book");
        }

        BookTransactionHistory bookTransactionHistory1 = bookTransactionHistory.findByBookIdAndOwnerId(bookId,user)
                .orElseThrow(()-> new OperationNotPermittedException("The book is not returned yet"));
        bookTransactionHistory1.setReturnedApproved(true);
       return bookTransactionHistory.save(bookTransactionHistory1).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("No book found with Id " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover  = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
