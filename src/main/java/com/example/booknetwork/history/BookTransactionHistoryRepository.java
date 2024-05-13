package com.example.booknetwork.history;

import com.example.booknetwork.book.Book;
import com.example.booknetwork.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory,Integer> {

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT 
            (COUNT(*)>0) AS isBorrowed
            FROM BookTransactionHistory bookTransActionHistory
            WHERE bookTransActionHistory.user.id = :userId
            AND bookTransActionHistory.book.id = :bookId
            AND bookTransActionHistory.returnedApproved = false 
         """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
        SELECT transaction
        FROM BookTransactionHistory transaction
        WHERE transaction.user.id = :userId
        AND transaction.book.id = :bookId
        AND transaction.returned = false
        AND transaction.returnedApproved = false
        """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, User userId);

    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.owner.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = true
            AND transaction.returnedApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, User userId);
}
