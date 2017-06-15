package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;
    
    // book copies determined had been "lost"
    private Set<BookCopy> missingCopies;
    
    // bookCollection Map<K,V> contains all books "bought":
    // K is a book and V is number of copies bought for that book
    private Map<Book, Integer> bookCollection;
    
    // lookupTable Map<K,V> is constructed by using the words from
    // both title and authors of a book, also the year of publication. 
    // Each word (K) is hashed to the set of books (V).
    private Map<String, Set<Book>> keywordLookupTable;
    
    // stop words and punctuation to be skipped during parsing text:
    // a an and but etc in is of on or the . , :
    private static final String[] stopWords = 
            new String[] {"a","an","and","but","de","etc","in","is","le","of","on","or","the","\'s"};
    private static final Set<String> patternsToSkip = 
            new HashSet<String>(Arrays.asList(stopWords));
    
    // flag
    private static enum UpdateAction {
        ADD, REMOVE
    };
    
    // rep invariant:
    // 1. the intersection of inLibrary and checkedOut is the empty set
    // 2. the bookCollection :
    //    a. contains books exactly (not BookCopy) from both inLibrary 
    //       and checkedOut sets (union of the two sets)
    //    b. number of copies of a book must equal to the sum of that book's copies in
    //       inLibrary and checkedOut (does not include missingCopies)
    //
    // 3. the keywordLookupTable entries <K,V> must:
    //    a. V must not be an empty set
    //    b. K must exist either in title or authors (case insensitive)
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // All reps are private so no rep exposure risk.
    
    public BigLibrary() {
        inLibrary = new HashSet<>();
        checkedOut = new HashSet<>();
        missingCopies = new HashSet<>();
        bookCollection = new HashMap<>();
        keywordLookupTable = new HashMap<>();
    }
    
    // assert the rep invariant
    private void checkRep() {
        if (!Collections.disjoint(inLibrary, checkedOut))
            throw new RuntimeException("intersection of inLibrary and checkedOut is not empty!");
        
        if (!checkBooksInBookCollection())
            throw new RuntimeException("bookCollection book count does not match!");
        
        if (!checkCopiesInBookCollection())
            throw new RuntimeException("bookCollection book copies count does not match!");
        
        if (!checkKeywordLookupTable())
            throw new RuntimeException("keywordLookupTable is not integrated!");
    }
    
    // check if bookCollection contains books exactly match the books from
    // both inLibrary and checkedOut (Book, not BookCopy)
    //
    private boolean checkBooksInBookCollection() {
        Set<Book> testCollection = new HashSet<>();
        
        for (BookCopy copy : inLibrary) {
            testCollection.add(copy.getBook());
        }
        
        for (BookCopy copy : checkedOut) {
            testCollection.add(copy.getBook());
        }
        
        if (testCollection.equals(bookCollection.keySet()))
            return true;
        else
            return false;
    }
    
    // check if each book in bookCollection contains copy count equals to sum of
    // the same book's copy count from inLibrary, checkedOut.
    //
    private boolean checkCopiesInBookCollection() {
        Book book;
        int count;
        
        for (Map.Entry<Book, Integer> entry : bookCollection.entrySet()) {
            count = 0;
            book = entry.getKey();
       
            for (BookCopy c : inLibrary) {
                if (book.equals(c.getBook()))
                        count++;
            }
        
            for (BookCopy c : checkedOut) {
                if (book.equals(c.getBook()))
                    count++;
            }
            
            if (count == entry.getValue()) // count match
                continue;
            else
                return false;
        }
        
        return true;
    }
  
    // check if keywordLookupTable is built correctly from the books in bookCollection
    //
    private boolean checkKeywordLookupTable() {
        String key, title, authors;
        Set<Book> books;
        
        if (keywordLookupTable.size() == 0) // no book bought yet
            return true;
        
        // check each entry <K,V> in the lookup table to ensure:
        // 1. V is not empty
        // 2. K indeed exists in every book in the set (either in title or authors)
        for (Map.Entry<String, Set<Book>> entry : keywordLookupTable.entrySet()) {
            key = entry.getKey();

            books = entry.getValue();
            for (Book b : books) {
                title = b.getTitle().toLowerCase();
                authors = b.getAuthors().toString().toLowerCase();

                if (title.contains(key))
                    continue;
                else if (authors.contains(key))
                    continue;
                else if (key.equals(String.valueOf(b.getYear())))
                    continue;
                else return false;
            }
        }
        
        return true;
    }

    @Override
    public BookCopy buy(Book book) {
        int copyCount = 0;
        
        assert (book != null);

        checkRep();
        BookCopy copy = new BookCopy(book);
        
        try {
            // add this book to book collection and update the copy count if
            // it's not the first copy bought
        
            if (bookCollection.containsKey(book)) {
                copyCount = bookCollection.get(book) + 1;
                bookCollection.put(book, copyCount);
            } else { // first copy of this book just bought
                bookCollection.put(book, 1);
                // add keywords to lookup table
                updateKeywordsLookupTable(book, UpdateAction.ADD);
            }
     
            // add this copy to book copy collection - inLibrary
            if (!inLibrary.add(copy)) {
                throw new RuntimeException("copy has been added!");
            }
            return copy;
        } finally {
            checkRep();
        }
    }
    
    // update all keywords from the book on the lookupTable depends on
    // the action (ADD or REMOVE)
    //
    private void updateKeywordsLookupTable(Book book, UpdateAction action) {
        String key;
        // convert the book description to a string
        String s = book.toString();
        
        // use whitespace as delimiter to retrieve key words from query
        Pattern pattern = Pattern.compile("\\W+");
        String[] keywords = pattern.split(s);
        
        // add (word, book) to the map <keyword, Set of books>
        // the "word" is the individual word from "title"+"authors"+"year"
        for (String word : keywords) {
            // first, convert the keyword to lowercase
            key = word.toLowerCase();
            
            //do nothing if this is a stop word
            if (patternsToSkip.contains(key))
                continue;
            
            // update the lookup table 
            updateLookupTable(key, book, action);
        }
        
        // add (word, book) to the map <keyword, Set of books>
        // the "word" is the string of "title" and author from "authors"
        String title = book.getTitle();
        updateLookupTable(title.toLowerCase(), book, action);
        
        for (String word : book.getAuthors())
            updateLookupTable(word.toLowerCase(), book, action);
    }
    
    /*
     * updateLookupTable
     * 
     * @param String word - keyword to be added to the lookup table as the key (K)
     * @param Book book - the book to be added to the set of books (V) associated with K
     * @param UpdateAction - ADD or REMOVE the keywords of this book to/from the keywordLookupTable
     */
    private void updateLookupTable(String word, Book book, UpdateAction action) {
        
        // ** test
        // System.out.println("(updateLookupTable) word= "+word+"; book= "+book.getTitle());
        
        // first retrieve the book set for matching this keyword
        Set<Book> bookSet = keywordLookupTable.get(word);
        
        if (bookSet == null) { // no key found, book set is empty
            if (action == UpdateAction.ADD) { // associate this book with the keyword
                bookSet = new HashSet<>();
                bookSet.add(book);
            } else { // it must be REMOVE, nothing needs to be done
                return;
            }
        }
        else { // found key
            if (action == UpdateAction.ADD) { // associate this book with the keyword
                bookSet.add(book);
            } else { // REMOVE - de-associate the book with this keyword
                bookSet.remove(book);
            }
        }
        
        // update <keyword, Set of books> to lookup table
        if (bookSet.isEmpty()) { // set is empty after removal of the book
            keywordLookupTable.remove(word);
        } else {
            keywordLookupTable.put(word, bookSet);
        }
    }
    
    @Override
    public void checkout(BookCopy copy) {
        assert (copy != null);
        
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
        assert (copy != null);
        
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
    public Set<BookCopy> allCopies(Book book) {
        assert (book != null);
        
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
        assert (book != null);
        
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
    public boolean isAvailable(BookCopy copy) {
        assert (copy != null);
        
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
    
    /*
     * working version of find() 
     *
    @Override
    public List<Book> find(String query) {

        HashMap<Book, Integer> sortedBooksFound, booksFound = new HashMap<>();
        Book book;
        List<Book> bookList = new LinkedList<>();
        int count = 0;
        Set<Book> books;
        String pattern = "\\W+";
        
        assert (query != null);
        
        checkRep();
        
        // use non-word character as delimiter to retrieve key words from query
        Pattern r = Pattern.compile(pattern);
        String[] keywords = r.split(query);
        System.out.println("query key words: " + Arrays.toString(keywords)); // ** test
        
        // use keyword to look up from the hash table keywordLookupTable
        // each keyword will get a set of books which contains the keyword
        for (String word : keywords) {   
            if (keywordLookupTable.containsKey(word)) {
                books = keywordLookupTable.get(word);
            } else { // this key does not exist in our lookup table
                continue;
            }
            
            // the reference counter = number of keyword match for this book
            // each book in the set should increment its reference counter by 1
            // booksFound is a map of <Book book, Integer count>
            for (Book b : books)  {
                if (booksFound.containsKey(b)) {
                    count = booksFound.get(b);
                    count++;
                    booksFound.put(b, count);
                } else {
                    // new entry
                    booksFound.put(b, 1);
                }
            }  // end of for (Book b : books)
        } // end of for (String word : keywords)
        
                    
        // reconstruct the booksFound list so that:
        //  <K, V> - K is "book" and the V is the combination
        // of count of keyword matched and the publication year - as the count
        // of is more significant than the year so we will construct
        // the V (to be sorted) as: count*10000 + year
        // eg: the match keyword count = 3, publication year = 1990
        //     => the V will be 31990
        for (Map.Entry<Book,Integer> entry : booksFound.entrySet()) {
            book = entry.getKey();
            count = entry.getValue();
            booksFound.replace(book, count*10000+book.getYear());
        }
        
        // iterate and sort the bookFound map in descending order to 
        // construct the returned book list
        sortedBooksFound = (HashMap<Book, Integer>) sortByValue(booksFound);
        for (Map.Entry<Book,Integer> entry : sortedBooksFound.entrySet()) {
            bookList.add(entry.getKey());
        }
        checkRep();
        return bookList;
    }
    */
    
    /**
     * This method will take each individual word from the input query string as
     * a keyword to conduct the search from book title, authors, or publication
     * year for a match.
     * The search is case insensitive, even though the books themselves are case
     * sensitive. Keywords can be entered either upper or lower case but the search
     * is performed case-insensitively. 
     * Multiple contiguous keywords can be entered by using opening and closing quotation marks.
     * 
     * find()
     * 
     * @param String query - a string of keywords to be searched for any match
     *                       on book title or authors. The commonly used stop words
     *                       are not included in the search.
     * @return a list of books found according to order of:
     * 1. exact title and authors match:
     *    a. if a complete title match then only the book matched will be returned.
     *    b. if a complete author match then all books related to that author will be returned.
     * 2. multiple contiguous words match - contiguous words can be represented between quotes
     * 3. more keywords match appear earlier
     * 4. if same book title and authors but different publication year, then
     *    the later one appears earlier
     */
    @Override
    public List<Book> find(String query) {

        HashMap<Book, Integer> sortedBooksFound, booksFound = new HashMap<>();
        Book book;
        List<Book> bookList = new LinkedList<>();
        int count = 0;
        boolean contiguousWords = false;
        Set<Book> books;
        // [^"]    - token starting with something other than "
        // \w+     - followed by one or more ASCII letter, digit or underscore
        // ...or...
        // ".+?"   - "whatever inside this quote pair"
        // final Pattern pattern = Pattern.compile("([^\"]\\w+|\".+?\")\\s*");
        final Pattern pattern = Pattern.compile("(\\w+|\".+?\")\\s*");
        Queue<String> keywords = new LinkedList<String>();
        String word, key;
        
        // long elapseTime = System.nanoTime(); // ** test
        
        assert (query != null);
        
        checkRep();
        
        // add all legit keywords in query string into a queue for processing
        Matcher m = pattern.matcher(query);    
        while (m.find()) {
            // ** test
            //for (int i = 1; i <= m.groupCount(); i++)
            //    System.out.print("group("+i+"): "+ m.group(i));
            //
            // keywords.add(m.group(1).replace("\"", "")); // used when quote pair to be removed
            keywords.add(m.group(1));
        }
       
        // remove keyword from queue to look up from the hash table keywordLookupTable
        // each keyword will get a set of books which contains the keyword
        while (!keywords.isEmpty()) {
            word = keywords.remove();
            key = word.toLowerCase();
            
            // if it is a stop word, do nothing. continue next keyword
            if (patternsToSkip.contains(key))
                continue;
            
            // if word is enclosed by quote pair "string", strip the quotes and
            // treat the whole string as a single key
            if (key.startsWith("\"")) {  // found opening quote, remove it
                contiguousWords = true;
                key = key.substring(1);
                if (key.endsWith("\""))  // found closing quote, remove it
                    key = key.substring(0, key.length()-1);
            }
            
            // System.out.println("key to look up: " + key); // ** test
            
            // retrieve the set of books associated with this keyword
            if (keywordLookupTable.containsKey(key)) {
                books = keywordLookupTable.get(key);
            } else { // this key does not exist in our lookup table
                // if it's a string of contiguous words, then we need to split it
                // into individual words and enqueue them for process later
                if (contiguousWords) {
                    contiguousWords = false;
                    m = pattern.matcher(key);      
                    while (m.find()) {
                        word = m.group(1);
                        if (patternsToSkip.contains(word))  // skip if a stop word found
                            continue;
                        keywords.add(word);
                    }
                }
                continue; // continue processing next keyword from the queue
            }  // end if (keywordLookupTable.containsKey(word))
            
            // the reference counter = number of keyword matched for this book
            // each book in the set should increment its reference counter by 1
            // booksFound is a map of <Book book, Integer count>
            for (Book b : books)  {
                if (booksFound.containsKey(b)) {
                    count = booksFound.get(b);
                    if (contiguousWords) { // whole string match, more weight given
                        count += 10;
                        contiguousWords = false;
                    } else 
                      count++;
                } else {
                    // new entry
                    if (contiguousWords) {
                        count = 10;
                        contiguousWords = false;
                    } else
                        count = 1;
                }
                booksFound.put(b, count);
            }  // end of for (Book b : books)
        } // end of while (!keywords.isEmpty())
        
        
        // reconstruct the booksFound list so that:
        //  <K, V> - K is "book" and the V is the combination
        // of count of keyword matched and the publication year - as the count
        // of is more significant than the year so we will construct
        // the V (to be sorted) as: count*10000 + year
        // eg: the match keyword count = 3, publication year = 1990
        //     => the V will be 31990
        for (Map.Entry<Book,Integer> entry : booksFound.entrySet()) {
            book = entry.getKey();
            count = entry.getValue();
            booksFound.replace(book, count*10000+book.getYear());
        }
        
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
        // System.out.println("Find(L) elapse time = "+(System.nanoTime()-elapseTime)); // ** test
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
           result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
     
    @Override
    public void lose(BookCopy copy) {
        Book book;
        int copyCount;
        
        assert (copy != null);
        
        checkRep();
        
        // remove this copy from both inLibrary and checkedOut sets
        if (!inLibrary.remove(copy)) {
            // not in library, see if it's been checked out
            if (!checkedOut.remove(copy)) {
                System.out.println("copy not exist in inLibrary and checkedOut!");
            }
        }
        
        // add this copy to missingCopies if it's not been added before
        if (!missingCopies.add(copy))
            System.out.println("copy has been added to missingCopies before!");
        
        // if both inLibrary and checkedOut does not have any more copy of 
        // this book then we should remove it from bookCollection as well.
        // Also, it will be removed from out lookupTable so that the "find"
        // won't find this book anymore.
        book = copy.getBook();
        if (bookCollection.containsKey(book)) {
            copyCount = bookCollection.get(book) - 1;
            if (copyCount <= 0) { // last copy of this book has been lost, remove it from bookCollection
                bookCollection.remove(book);
                
                // add keywords to lookup table
                updateKeywordsLookupTable(book, UpdateAction.REMOVE);
            }
            else
                bookCollection.put(book, copyCount);
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
