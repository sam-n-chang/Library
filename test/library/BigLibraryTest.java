package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {
    
    /* 
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java 
     */

    /*
     * Testing strategy
     * ==================
     *
     * This method will take each individual word from the input query string as
     * a keyword to conduct the search from book title, authors, or publication
     * year for a match.
     * The search is case insensitive, even though the books themselves are case
     * sensitive. 
     * 
     * find()
     * 
     * @param String query - a string of keywords to be searched for any match
     *  on book title or authors. Keywords can be entered either upper or lower case 
     *  but the search is performed case-insensitively. 
     *  Multiple contiguous keywords can be entered by using opening and closing 
     *  quotation marks ("keywords string...").
     *  
     * @return a list of books found according to order of:
     * 1. exact title and authors match (i.e., if a copy of a book is in the library's 
     *    collection, then find(book.getTitle()) and find(book.getAuthors().get(i)) 
     *    must include book among the results).
     * 2. multiple contiguous words match - contiguous words can be represented between quotes
     * 3. more keywords match appear earlier.
     * 4. if same book title and authors but different publication year, then
     *    the newer one appears earlier.
     * 5. a book should appear at most once on the list. 
     * 
     * Partition the inputs as follows:
     * query - null / valid string with the following:
     * 
     * << following are implemented in this file in addition to these in LibraryTest.java>>
     * test books found according to order of:
     * 1. exact title and authors match:
     *    a. if a complete title match then only the book matched will be returned.
     *    b. if a complete author match then all books related to that author will be returned.
     * 2. multiple contiguous words match - contiguous words can be represented between quotes
     * 3. more keywords matched appear earlier
     * 4. if same book title and authors but different publication year, then
     *    the newer one appears earlier
     *    
     * other tests:
     * 5. stop words testing
     * 6. large numbers of books search
     * 
     * << following are implemented in LibraryTest.java>>
     * 
     * 0 key word match from both title and author - also include case sensitivity
     * 1 key word match from title / author - checked out and non checked out
     * 2 key words match - one from title and one from author
     * 2 key words match - both from title
     * 2 key words match - both from author
     * 2 key words match - each for a different book
     * 3 key words - one book with 3 matches and one with 2 matches - check ordering
     * 
     * 1 book with multiple copies - should be just one returned
     * 3 books with same title and authors except publication year
     * 
     * multiple book copies in library collection, use find(book.getTitle()) 
     * and find(book.getAuthors().get(i)) to find a book.
     *          
     * Cover each part testing coverage.
     */
    @Test
    public void bigLibrarytestExactTitleString() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        library.checkout(copy);
        List<Book> books = library.find("\"Harry Potter and the Deathly Hollows\"");
        
        // should return the exact match
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void bigLibrarytestCompleteTitle() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        library.checkout(copy);
        List<Book> books = library.find("Harry Potter and the Deathly Hollows");
        
        // should return all possible match
        assertTrue("Should find 2 book", books.size() == 2);
    }
    
    @Test
    public void bigLibrarytestCompleteSingleAuthor() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        library.checkout(copy);
        List<Book> books = library.find("J.K. Rowling");
        
        assertTrue("Should find 2 book", books.size() == 2);
        assertTrue("Wrong books order", books.get(0).getYear() == 2007);
    }
    
    @Test
    public void bigLibrarytestCompleteMutipleAuthors() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        Book book5 = new Book("Harry Potter and his stories", Arrays.asList("J.K. Rowling", "No Name"), 2000);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);
        library.buy(book5);

        library.checkout(copy);
        List<Book> books = library.find("J.K. Rowling no name");
        
        assertTrue("Should find 3 book", books.size() == 3);
        assertTrue("Wrong books order", books.get(0).getTitle().equals("Harry Potter and his stories"));
    }

    @Test
    public void bigLibrarytestPartialTitleString() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        library.checkout(copy);
        List<Book> books = library.find("\"Harry Potter\"");
        
        assertTrue("Should find 2 book", books.size() == 2);
        assertTrue("Wrong books order", books.get(0).getYear() == 2007);
    }

    @Test
    public void bigLibrarytestPartialTitleStringWithStopWords() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        library.checkout(copy);
        List<Book> books = library.find("\"Harry Potter and the Philosopher's\"");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    

    @Test
    public void bigLibrarytestOnlyStopWords() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        library.checkout(copy);
        List<Book> books = library.find("the of a");
        
        assertTrue("Should find 0 book", books.size() == 0);
    }

    @Test
    public void bigLibrarytestOneWordInQuotationMarks() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        library.checkout(copy);
        List<Book> books = library.find("\"Quixote\"");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void bigLibrarytestMultipleWordInQuotationMarksTitlePartial() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Half-Blood Prince", Arrays.asList("J.K. Rowling"), 2005);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        library.checkout(copy);
        // List<Book> books = library.find("\"Don Quixote\"");
        List<Book> books = library.find("\"Harry Potter\"");
        
        assertTrue("Should find 2 book", books.size() == 2);
        assertTrue("Wrong book order", books.get(0).getYear() == 2005);
    }

    @Test
    public void bigLibrarytestCaseInsensitive() {
        Library library = new BigLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        library.checkout(copy);
        List<Book> books = library.find("harry potter");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    

    @Test
    public void bigLibrarytestLargeNumberBooks() {
        Library library = new BigLibrary();
        
        Book[] bookList = new Book[100];
        
        for (int i=0; i < 100; i++) {
            bookList[i] = new Book("Story Book, Vol: "+(i+1),  Arrays.asList("No Name"), 1911);
            library.buy(bookList[i]);
        }
        
        List<Book> books = library.find("10");
        // System.out.println("book: "+books.get(0).toString());
        
        assertTrue("Should find 1 book", books.size() == 1);
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
