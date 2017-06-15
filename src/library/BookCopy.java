package library;

import static library.BookCopy.Condition.*;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 * 
 * Specification fields:
 * @specfield book : Book          // The book of this book copy referencing to
 * @specfield condition: Condition // The condition of this book copy
 * 
 * Abstraction Invariant:
 *    book != null 
 *    book condition can be only either GOOD or DAMAGED
 *    
 */
public class BookCopy {
    
    // rep
    private final Book book;
    private Condition condition;
    
    // rep invariant:
    //    book != null
    //    condition can be only either GOOD or DAMAGED
    //
    // abstraction function:
    //    AF(r) = BookCopy copy such that
    //       copy.book = r.book + condition
    //
    // rep book is private so no exposure risk
    
    public static enum Condition {
        GOOD, DAMAGED
    };
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = book;
        this.condition = GOOD;
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        if ((book == null))
            throw new RuntimeException("invalid book");
    }
    
    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        checkRep();
        return book;
    }
    
    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        checkRep();

        return condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        checkRep();
        this.condition = condition;
        checkRep();
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    @Override
    public String toString() {
        String bookCondition;
        
        bookCondition = (this.getCondition() == GOOD) ? "good)" : "damaged)";
        
        return this.getBook().toString() + " (condition: " + bookCondition;
    }

    // uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't
    // @Override
    // public boolean equals(Object that) {
    //     throw new RuntimeException("not implemented yet");
    // }
    // 
    // @Override
    // public int hashCode() {
    //     throw new RuntimeException("not implemented yet");
    // }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
