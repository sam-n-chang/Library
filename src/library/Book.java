package library;

import java.util.List;
import java.util.ArrayList;

import java.util.Calendar;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object, 
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author 
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 * 
 * Specification fields:
 * @specfield title : String         // The title of the book
 * @specfield authors: List<String>  // The list of authors of the book
 * @specfield year: int              // The year the book published
 * 
 * Abstraction Invariant:
 *    title != null && title != empty string && 
 *          must contain at least one non-space character
 *    authors != null && authors != empty list &&
 *          author name must contain at least one non-space character
 *          Alphabetic case and author order are significant
 *    0 < year <= present year
 */
public class Book {

    // rep
    private final String title;
    private final List<String> authors;
    private final int year;
    
    // rep invariant:
    //    title != null && title != "" && must contain at least one non-space character
    //    authors != null && !authors.isEmpty() &&
    //            author name must contain at least one non-space character
    //            alphabetic case and author order are significant
    //    0 < year < present year
    //
    // abstraction function:
    //    AF(r) = Book b such that
    //       b.title = r.title
    //       b.authors = r.authors
    //       b.year = r.year
    //
    // All reps are private so no rep exposure risk.
    
    /**
     * Make a Book.
     * 
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain 
     * at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative. 
     */
    public Book(final String title, final List<String> authors, final int year) {
        this.title = title;
        this.authors = new ArrayList<String>(); // mutable
        this.authors.addAll(authors); 
        this.year = year;
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        if (year <= 0 || year > Calendar.getInstance().get(Calendar.YEAR))
            throw new RuntimeException("year is out of range: " + year);
        
        if ((title == null) || (title.isEmpty()) || !checkName(title))
            throw new RuntimeException("invalid book title");
        
        if ((authors == null) || (authors.isEmpty()))
            throw new RuntimeException("invalid book authors list");
        
        // if any one of authors' name is legit
        boolean nameIsOk = false;
        
        for (String n : authors) {
            if (checkName (n))
                nameIsOk = true;
        }
        if (!nameIsOk)
            throw new RuntimeException("invalid book author name on list");
    }
    
    // assert the name is legit - at least one non-space character.
    // return true if it is.
    private boolean checkName(String name){
        // check if there is One character that is not a whitespace character
        // as defined by \s
        if (name.trim().length() > 0) 
           return true;
        else 
            return false;
    }
    
    /**
     * @return the title of this book
     */
    public String getTitle() {
        checkRep ();
        return title;
    }
    
    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        List<String> authors = new ArrayList<String>(); // mutable

        checkRep ();
        authors.addAll(this.authors);
        return authors;
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        checkRep ();
        return year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    @Override 
    public String toString() {
        return this.getTitle()
                + this.getAuthors()
                + this.getYear();
    }

    @Override
    public boolean equals(Object thatObject) {  
        
        if (!(thatObject instanceof Book))
           return false;
        
        if (this == thatObject)
                return true;

        Book that = (Book) thatObject;
        
        if (!this.getTitle().equals(that.getTitle()))
            return false;
        
        if (this.getYear() != that.getYear())
            return false;
        
        if (!this.getAuthors().equals(that.getAuthors()))
            return false;
        
        return true;
    }
     
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        
        // compute the year first
        result = prime * result + this.getYear(); 
        
        // compute the title string
        result = result * prime + this.getTitle().hashCode();
        
        // iterate the authors list
        for (String author : this.getAuthors())
        {
            result = result * prime + author.hashCode();
        }

        return result;
    }



    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
