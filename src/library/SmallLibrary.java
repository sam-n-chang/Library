package library;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.lang.System;


/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;
    
    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // All reps are private so no rep exposure risk.
    
    public SmallLibrary() {
        inLibrary = new HashSet<BookCopy>();
        checkedOut = new HashSet<BookCopy>();
    }
    
    // assert the rep invariant
    private void checkRep() {
        Collections.disjoint(inLibrary, checkedOut);
    }

    @Override
    public BookCopy buy(Book book) {
        if (book == null)
            throw new RuntimeException("null ptr encountered for buy");
        checkRep();
        BookCopy copy = new BookCopy(book);
        
        try {
            // add this copy to the library's collection - inLibrary
            if (!inLibrary.add(copy)) {
                throw new RuntimeException("copy has been added!");
            }
            return copy;
        } finally {
            checkRep();
        }
    }
    
    @Override
    public void checkout(BookCopy copy) {
        if (copy == null)
            throw new RuntimeException("null ptr encountered for checkout");
        checkRep();
        
        // before the copy is checked out, it has to be in inLibrary
        if (inLibrary.contains(copy)) {
            inLibrary.remove(copy);
            checkedOut.add(copy);
        } else {
            // System.out.println("copy not in library, can not be checked out!"); // ** test
        }
        checkRep();
    }
    
    @Override
    public void checkin(BookCopy copy) {
        if (copy == null)
            throw new RuntimeException("null ptr encountered for checkin");
        checkRep();
        // before the copy is checked in, it has to be in checkedOut
        if (checkedOut.contains(copy)) {
            checkedOut.remove(copy);
            inLibrary.add(copy);
        } else {
            // System.out.println("copy not checked out, can not be checked in!"); // ** test
        }
        checkRep();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        if (copy == null)
            throw new RuntimeException("null ptr encountered for isAvailable");
        checkRep();
        try {
            if (inLibrary.contains(copy))
                return true;
            else
                return false;
        } finally {
            checkRep();
        }
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        if (book == null)
            throw new RuntimeException("null ptr encountered for allCopies");
        checkRep();
        Set<BookCopy> all = new HashSet<BookCopy>();
        
        // iterate over both inLibrary and checkedOut sets
        // to see if any copy matching with the book
        for (BookCopy copy : inLibrary) 
            if (copy.getBook().equals(book)) {
                all.add(copy);
            }
        for (BookCopy copy : checkedOut) {
            if (copy.getBook().equals(book))
                all.add(copy);
        }
        checkRep();
        return all;
    }
    
    @Override
    public Set<BookCopy> availableCopies(Book book) {
        if (book == null)
            throw new RuntimeException("null ptr encountered for availableCopies");
        checkRep();
        Set<BookCopy> copies = new HashSet<BookCopy>();
        
        // iterate over inLibrary set to see if any copy matching with the book
        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book))
                copies.add(copy);
        }
        checkRep();
        return copies;
    }

    @Override
    public List<Book> find(String query) {

        HashMap<Book, Integer> sortedBooksFound, booksFound = new HashMap<>();
        Book book;
        List<Book> bookList;
        int count = 0;
        
        // long elapseTime = System.nanoTime(); // ** test
        
        if (query == null)
            throw new RuntimeException("null ptr encountered for find");
        checkRep();
        
        // use non-word character as delimiter to retrieve key words from query
        Pattern pattern = Pattern.compile("\\W+");
        String[] keywords = pattern.split(query);
        // System.out.println("query key words: " + Arrays.toString(keywords)); // ** test
        
        // search each book copy in inLibrary group
        for (BookCopy copy : inLibrary) {   
            book = copy.getBook();
            // find out how many keywords match
            count = countKeywordInBookCopy(book, keywords);
            
            // if keyword(s) was found then add the book to our list
            // note that the K is "book" and the V is the combination
            // of count of keyword matched and the publication year - as the count
            // of is more significant than the year so we will construct
            // the V (to be sorted) as: count*10000 + year
            // eg: the match keyword count = 3, publication year = 1990
            //     => the V will be 31990
            if (count > 0) {
               booksFound.putIfAbsent(book, count*10000+book.getYear());
            }
        }  // for each copy in checkedOut
        
        // search each copy in checkedOut group
        for (BookCopy copy : checkedOut) {   
            book = copy.getBook();
            count = countKeywordInBookCopy(book, keywords);

            if (count > 0) {
               booksFound.putIfAbsent(book, count*10000+book.getYear());
            }
        }  // for each copy in checkedOut
        
        // iterate and sort the bookFound map in descending order to 
        // construct the returned book list
        sortedBooksFound = (HashMap<Book, Integer>) sortByValue(booksFound);
        bookList = new ArrayList<Book>(sortedBooksFound.keySet());
        /*
        for (Map.Entry<Book,Integer> entry : sortedBooksFound.entrySet()) {
            bookList.add(entry.getKey());
        }
        */
        checkRep();
        // System.out.println("Find(S) elapse time = "+(System.nanoTime()-elapseTime)); // ** test
        return bookList;
    }
    /*
     * sortByValue - Sort a Map<Key, Value> by values
     * 
     * @param Map<K, V> map - map to be sorted according to its entry values
     * @return Map<K, V> - sorted map in descending order
     * 
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>()
                           {
                               @Override
                               public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
                               {
                                   return (o2.getValue()).compareTo(o1.getValue());
                               }
                           } );

        // Hash table and linked list implementation of the Map interface, 
        // with predictable iteration order
        Map<K, V> result = new LinkedHashMap<>();
        
        for (Map.Entry<K, V> entry : list)
        {
           result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
    
    /*
     * parse both title and author list of bookcopy copy according to the keyword
     * 
     * @param Book book - book  to be parsed
     * @param String[] keywords - arrary of keywords used in the search
     * @return int count - how many times the keyword show in both the title and author list
     */
    private int countKeywordInBookCopy (Book book, String[] keywords) {
        String titleAuthorString;
        int count = 0;

        if (book == null)
            return 0;
        
        // ignore case during lookup
        titleAuthorString = book.toString().toLowerCase();
        
        for (int i = 0; i < keywords.length; i++)  {
            if (titleAuthorString.contains(keywords[i].toLowerCase())) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public void lose(BookCopy copy) {
        if (copy == null)
            throw new RuntimeException("null ptr encountered for lose");
        
        checkRep();
        // remove this copy from both inLibrary and checkedOut sets
        if (!inLibrary.remove(copy)) {
            // not in library, see if it's been checked out
            if (!checkedOut.remove(copy)) {
                System.out.println("copy not exist in record");
            }
        }
        checkRep();
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
