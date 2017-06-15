package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "library.SmallLibrary", 
            "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /*
     * Testing strategy
     * ==================
     * 
     *
     * Buy a new copy of a book and add it to the library's collection.
     *
     * BookCopy buy(Book book)
     * 
     * @param book Book to buy
     * @return a new, good-condition copy of the book, which is now available in this library
     * 
     * Partition the inputs as follows:
     * book - valid book
     *          
     * Cover each part testing coverage.
     */
   
    @Test
    public void testBuyGoodBook() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);

        BookCopy copy = library.buy(book);
        // System.out.println(copy.toString()); // ** test
        assertEquals("book copy is not consistent with book bought", book, copy.getBook());
    }
    
    /*
     * Testing strategy
     * ==================
     * 
     * Test whether a book copy is available in this library.
     *      
     * boolean isAvailable(BookCopy copy);
     * 
     * @param copy Book copy to test
     * @return true if and only if copy is available in this library
     * 
     * Partition the inputs as follows:
     * copy - valid book but not bought / valid book and bought by library
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testAvailableBookNotBought() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);

        assertTrue("book should not be found in library", !library.isAvailable(new BookCopy(book)));
    }
    
    @Test
    public void testAvailableBookBought() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);

        BookCopy copy = library.buy(book);
        assertTrue("book not found in library", library.isAvailable(copy));
    }
    
    /*
     * Testing strategy
     * ==================
     *
     * Check out a copy of a book.
     * 
     * checkout(BookCopy copy)
     * 
     * @param copy Copy to check out. Requires that the copy be available in this library.
     *
     * Partition the inputs as follows:
     * copy - copy available / copy not available
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testCheckoutCopyNotAvailable() {
        Library library = makeLibrary();

        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = new BookCopy(book);
        library.checkout(copy);

        assertTrue("testCheckoutCopyNotAvailable", !library.isAvailable(copy));
    }
    
    @Test
    public void testCheckoutCopyAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        
        BookCopy copy = library.buy(book);
        library.checkout(copy);

        assertTrue("book has been checked out", !library.isAvailable(copy));
    }
    /*
     * Testing strategy
     * ==================
     *
     * Check in a copy of a book, making it available again
     * 
     * void checkin(BookCopy copy)
     * 
     * @param copy Copy to check in.  Requires that the copy be checked out of this library.
     *
     * Partition the inputs as follows:
     * copy - copy not bought
     *        copy bought and checked out / copy bought but not checked out
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testCheckinCopyNotBought() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = new BookCopy(book);
        library.checkin(copy);
        assertTrue("book was not bought", !library.isAvailable(copy));
    }
    
    @Test
    public void testCheckinCopyNotCheckedOut() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = library.buy(book);
        library.checkin(copy);
        assertTrue("book should be available", library.isAvailable(copy));
    }
  
    @Test
    public void testCheckinCopyCheckedOut() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = library.buy(book);
        library.checkout(copy);
        library.checkin(copy);
        assertTrue("book was checked out and checked in again", library.isAvailable(copy));
    }
    
    /*
     * Testing strategy
     * ==================
     *
     * Get all the copies of a book
     * 
     * Set<BookCopy> allCopies(Book book)
     * 
     * @param book Book to find
     * @return set of all copies of the book in this library's collection, both available and checked out.
     *
     * Partition the inputs as follows:
     * book - valid book
     * check 0 copy
     * check 2 copies: 2 available
     * check 2 copies: 1 available, 1 checked out
     * check 2 copies: 0 available, 2 checked out
     *          
     * Cover each part testing coverage.
     */
   
    @Test
    public void testAllCopiesEmptySet() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);

        assertTrue("book set should be empty", library.allCopies(book).isEmpty());
    }
    
    @Test
    public void testAllCopiesSet2CopiesAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        library.buy(book);
        library.buy(book);

        assertTrue("book set should have 2 copies", library.allCopies(book).size() == 2);
    }
    
    @Test
    public void testAllCopiesSet1CopyAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = library.buy(book);
        library.buy(book);
        library.checkout(copy);

        assertTrue("book set should have 2 copies", library.allCopies(book).size() == 2);
    }
    
    @Test
    public void testAllCopiesSet0CopyAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);
        library.checkout(copy1);
        library.checkout(copy2);

        assertTrue("book set should have 2 copies", library.allCopies(book).size() == 2);
    }
    /*
     * Testing strategy
     * ==================
     *
     * Get all the available copies of a book
     * 
     * Set<BookCopy> availableCopies(Book book)
     * 
     * @param book Book to find
     * @return set of all copies of the book that are available in this library.
     *
     * Partition the inputs as follows:
     * book - valid book
     * check 0 copy
     * check 2 copies: 2 available
     * check 2 copies: 1 available, 1 checked out
     * check 2 copies: 0 available, 2 checked out
     *          
     * Cover each part testing coverage.
     */
   
    @Test
    public void testAvailableCopiesEmptySet() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);

        assertTrue("book set should be empty", library.availableCopies(book).isEmpty());
    }
    
    @Test
    public void testAvailableCopies2CopiesAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        library.buy(book);
        library.buy(book);

        assertTrue("book set should have 2 copies", library.availableCopies(book).size() == 2);
    }
    
    @Test
    public void testAvailableCopies1CopyAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = library.buy(book);
        library.buy(book);
        library.checkout(copy);

        assertTrue("book set should have 1 copies", library.availableCopies(book).size() == 1);
    }
    
    @Test
    public void testAvailableCopies0CopyAvailable() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);
        library.checkout(copy1);
        library.checkout(copy2);

        assertTrue("book set should have 0 copies", library.availableCopies(book).size() == 0);
    }
    /*
     * Testing strategy
     * ==================
     *
     * Declare a copy of a book as lost from the library.  A copy can be declared lost if it is stolen
     * without being checked out, or if a borrower checks it out but never returns it. 
     *
     * void lose(BookCopy copy);
     *
     * @param copy BookCopy to declare lost.  Must have been previously returned from buy() on this library.
     *
     * Partition the inputs as follows:
     * copy - valid bookcopy
     *          
     * Cover each part testing coverage.
     */
    
    @Test
    public void testLoseBookCopy() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy = library.buy(book);

        assertTrue (library.isAvailable(copy));  // make sure the copy exists first
        library.lose(copy);

        assertTrue("book copy has been lost", !library.isAvailable(copy));
    }
    
    
    @Test
    public void testLoseMultipleBookCopy() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        BookCopy copy1 = library.buy(book);
        BookCopy copy2 = library.buy(book);

        assertTrue (library.isAvailable(copy1));  // make sure the copy exists first
        assertTrue (library.isAvailable(copy2));

        library.lose(copy1);
        library.lose(copy2);

        assertTrue("book copy 1 has been lost", !library.isAvailable(copy1));
        assertTrue("book copy 2 has been lost", !library.isAvailable(copy2));
    }
    /*
     * Testing strategy
     * ==================
     *
     * Search for books in this library's collection.
     * 
     * List<Book> find(String query)
     * 
     * @param query search string
     * @return list of books in this library's collection (both available and checked out) 
     * whose title or author match the search string, ordered by decreasing amount  of match.
     * A book should appear at most once on the list. 
     * Keyword matching and ranking is underdetermined, but at the very least must support: 
     *     - exact matching of title and author: i.e., if a copy of a book is in the library's 
     *           collection, then find(book.getTitle()) and find(book.getAuthors().get(i)) 
     *           must include book among the results.
     *     - date ordering: if two matching books have the same title and author but different
     *           publication dates, then the newer book should appear earlier on the list. 
     *
     * Partition the inputs as follows:
     * query - valid string with the following:
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
    public void testFind0keyWordMatch() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        library.buy(book);

        List<Book> books = library.find("Frank");

        assertTrue("Should find no book available", books.size() == 0);
    }
    
    @Test
    public void testFind0keyWordMatchCaseSensitivity() {
        Library library = makeLibrary();
        Book book = new Book("A New World Power", Arrays.asList("Steve Hawks", "Thomas King"), 2000);
        library.buy(book);

        List<Book> books = library.find("THOMAS");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind1keyWordMatchAuthor() {
        Library library = makeLibrary();

        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        List<Book> books = library.find("Dickens");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind1keyWordMatchTitle() {
        Library library = makeLibrary();

        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        List<Book> books = library.find("Potter");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind1keyWordMatchCheckedOut() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        BookCopy copy = library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        library.checkout(copy);
        List<Book> books = library.find("Quixote");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind2keyWordsMatchTitle() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        List<Book> books = library.find("Two Cities");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind2keyWordsMatchAuthor() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        List<Book> books = library.find("Charles Dickens");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
    
    @Test
    public void testFind2keyWordsMatchTitleAndAuthor() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        
        List<Book> books = library.find("Don Miguel");
        
        assertTrue("Should find 1 book", books.size() == 1);
    }
  
    @Test
    public void testFind2keyWordsMatch2Books() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);

        List<Book> books = library.find("Cities Rowling");

        assertTrue("Should find 2 book", books.size() == 2);
    }

    @Test
    public void testFindOneBookMultipleCopies() {
        Library library = makeLibrary();
        
        new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        library.buy(book3);
        library.buy(book3);
        library.buy(book3);

        List<Book> books = library.find("Harry Potter");
        
        assertTrue("Should find 1 book, but found: " + books.size(), books.size() == 1);
    }

    @Test
    public void testFind3keyWordsMatch3Books() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Harry Potter and the Half-Blood Prince", Arrays.asList("J.K. Rowling"), 2005);
        Book book2 = new Book("A Tale of Two Cities", Arrays.asList("Charles Dickens"), 1859);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Harry Potter and the Deathly Hollows", Arrays.asList("J.K. Rowling"), 2007);
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);

        String query = "Harry Potter";
        List<Book> books = library.find(query);
        
        // ** test
        // System.out.println("<query string> "+query);
        // for (int i = 0; i < books.size(); i++) {
        //     System.out.println("matched books: "+books.get(i).getTitle());
        // }
        
        assertTrue("Should find 2 book", books.size() == 3);
        assertTrue("Order is not correct", 
                   books.get(0).getTitle().equals("Harry Potter and the Deathly Hollows"));
    }
  
    @Test
    public void testFindSameBooksExceptYear() {
        Library library = makeLibrary();
        
        Book book2 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 2000);
        Book book3 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);
        Book book4 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1950);
        library.buy(book3);
        library.buy(book1);
        library.buy(book4);
        library.buy(book2);

        List<Book> books = library.find("Don Quixote");
        
        assertTrue("Should find 3 books", books.size() == 3);
        assertTrue("Order is not correct", books.get(0).getYear() == 2000);
    }
    //
    // multiple book copies in library collection, use find(book.getTitle()) 
    // to find a book.
    //

    @Test
    public void testFindBookByExactTitle() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);

        library.buy(book2);
        library.buy(book1);

        List<Book> books = library.find(book1.getTitle());
        
        assertTrue("Should find 1 books", books.size() == 1);
    }
    //
    // multiple book copies in library collection, use 
    // find(book.getAuthors().get(i)) to find a book.
    //
    
    @Test
    public void testFindBookByExactAuthor() {
        Library library = makeLibrary();
        
        Book book1 = new Book("Don Quixote", Arrays.asList("Miguel de Cervantes"), 1612);
        Book book2 = new Book("Harry Potter and the Philosopher's Stone", Arrays.asList("J.K. Rowling"), 1997);

        library.buy(book2);
        library.buy(book1);

        List<Book> books = library.find(book2.getAuthors().get(0));
        
        assertTrue("Should find 1 books", books.size() == 1);
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
