package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

import static library.BookCopy.Condition.*;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy
     * ==================
     * 
     * @param book : Book          // The book of this book copy referencing to
     * @param condition: Condition // The condition of this book copy
     * 
     * Partition the inputs as follows:
     * book - null / valid book
     * condition - GOOD, DAMAGED
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testBookCopyNullTest() {

        boolean thrown = false;
        
        try {
            new BookCopy(null);
          } catch (RuntimeException e) {
            thrown = true;
          }

        assertTrue("Exception expected in testBookCopyNullTest", thrown);
    }
    
    @Test
    public void testOkSetGetConditionTest() {
        Book book = new Book("New China", Arrays.asList("Z.T. Mao", "K.S. Chiang"), 2001);
        BookCopy copy = new BookCopy(book);
        copy.setCondition(DAMAGED);
        // System.out.println(copy.toString());
        assertEquals(copy.getCondition(), DAMAGED);
    }
    
    @Test
    public void testOkGetBookTest() {
        Book book = new Book("New China", Arrays.asList("Z.T. Mao", "K.S. Chiang"), 2001);
        BookCopy copy = new BookCopy(book);
        // System.out.println(copy.toString());
        assertEquals(book, copy.getBook());
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
