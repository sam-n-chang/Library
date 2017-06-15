package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.Year;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     * 
     * @param title - Title of the book. Must contain at least one non-space character.
     * @param authors - Names of the authors of the book.  Must have at least one name, and each name must contain 
     * at least one non-space character.
     * @param year - Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative. 
     * 
     * Partition the inputs as follows:
     * title - null / empty string / string without space character / 
     *         string with one space character
     * authors - null / empty list / list with one author / list with two authors
     *           different alphabetic case test
     *           different author order test
     * year - negative, 0, present year, future year
     *          
     * Cover each part testing coverage.
     */
    
    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    @Test
    public void testBookConstructor() {
        List<String> authors = new ArrayList<>();
        authors.add("Z.T. Mao");

        Book book = new Book("New China", authors, 1990);
        assertEquals(Arrays.asList("Z.T. Mao"), book.getAuthors());

        // mutating authors shouldn't change the immutable Book
        authors.add("no name");
        assertEquals(Arrays.asList("Z.T. Mao"), book.getAuthors());
    }
    
    @Test
    public void testGetTitleNullString() {
        
        boolean thrown = false;
        
        try {
            new Book(null, Arrays.asList("no name"), 1990);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetTitleNullString", thrown);
    }
    
    @Test
    public void testGetTitleEmptyString() {
        
        boolean thrown = false;
        
        try {
            new Book("", Arrays.asList("no name"), 1990);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetTitleEmptyString", thrown);
    }
    
    @Test
    public void testGetTitleOnlySpaceString() {
        
        boolean thrown = false;
        
        try {
            new Book("     ", Arrays.asList("no name"), 1990);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetTitleOnlySpaceString", thrown);
    }
 
    @Test
    public void testGetTitleOkString() {
        Book book = new Book("New China", Arrays.asList("no name"), 1990);
        assertEquals ("Title did not match", "New China", book.getTitle());
    }
    
    @Test
    public void testGetAuthorsNullString() {
        
        boolean thrown = false;
        
        try {
            new Book("New China", null, 1990);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetAuthorsNullString", thrown);
    }
    
    @Test
    public void testGetAuthorsEmptyString() {
        
        boolean thrown = false;
        
        try {
            new Book("New China", Arrays.asList(""), 1990);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetAuthorsEmptyString", thrown);
    }
    
    @Test
    public void testGetAuthorsOkEmptyString() {
        
        Book book = new Book("New China", Arrays.asList("No Name", ""), 1990);
        assertTrue ("Author list did not match", 
                    book.getAuthors().equals(Arrays.asList("No Name", "")));
    }
 
    @Test
    public void testGetAuthorsOkTwoAuthors() {
        
        Book book = new Book("New China", Arrays.asList("Z.T. Mao", "K-S Chiang"), 1990);
        assertTrue ("Author list did not match", 
                    book.getAuthors().equals(Arrays.asList("Z.T. Mao", "K-S Chiang")));
    }
    
    @Test
    public void testGetAuthorsCaseSensitive() {
        
        Book book1 = new Book("New China", Arrays.asList("Mao"), 1990);
        Book book2 = new Book("New China", Arrays.asList("MAO"), 1990);
        assertTrue ("Authors are different - case sensitivity", !book1.equals(book2));
    }
    
    @Test
    public void testGetAuthorsDifferentOrder() {
        
        Book book1 = new Book("New China", Arrays.asList("Mao", "Chiang"), 1990);
        Book book2 = new Book("New China", Arrays.asList("Chiang", "Mao"), 1990);
        
        // two books are NOT equal authors are in different order
        assertTrue ("Authors are different - order", !book1.equals(book2));
    }
    
    @Test
    public void testGetAuthorsOkSameOrder() {
        
        Book book1 = new Book("New China", Arrays.asList("Mao", "Chiang"), 1990);
        Book book2 = new Book("New China", Arrays.asList("Mao", "Chiang"), 1990);
        
        // two books are equal authors are in the same order
        assertTrue ("Authors are different - order", book1.equals(book2));
    }
    
    @Test
    public void testGetAuthorsSameOrderDifferentCase() {
        
        Book book1 = new Book("New China", Arrays.asList("MAO", "CHIANG"), 1990);
        Book book2 = new Book("New China", Arrays.asList("Mao", "Chiang"), 1990);
        
        // two books are NOT equal authors are in the same order, but different case
        assertTrue ("Authors are different - order", !book1.equals(book2));
    }
   
    @Test
    public void testGetYearValueZero() {
        
        boolean thrown = false;
        
        try {
            new Book("New China", Arrays.asList("no name"), 0);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetYearValueZero", thrown);
    }
    
    @Test
    public void testGetYearValueNegative() {
        
        boolean thrown = false;
        
        try {
            new Book("New China", Arrays.asList("no name"), -1988);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetYearValueNegative", thrown);
    }
    
    @Test
    public void testGetYearFutureTime() {
        
        boolean thrown = false;
        
        try {
            new Book("New China", Arrays.asList("no name"), Year.now().getValue()+1);
          } catch (RuntimeException e) {
            thrown = true;
          }

          assertTrue("Exception expected in testGetYearFutureTime", thrown);
    }
    
    @Test
    public void testGetYearOkPresentYear() {
        
        Book book = new Book("New China", Arrays.asList("no name"), Year.now().getValue());

        assertTrue("Wrong year", book.getYear() == Year.now().getValue());
    }
    
    
    @Test
    public void testBookToString() {
        
        Book book = new Book("New China", Arrays.asList("no name"), Year.now().getValue());
        System.out.println(book.toString());
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
