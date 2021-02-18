package com.cursor.library.services;

import com.cursor.library.daos.BookDao;
import com.cursor.library.exceptions.BadIdException;
import com.cursor.library.exceptions.BookNameIsNullException;
import com.cursor.library.exceptions.BookNameIsTooLongException;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookService bookService;
    private BookDao bookDao = new BookDao();


    @BeforeAll
    void setUp() {
        bookDao = Mockito.mock(BookDao.class);
        bookService = new BookService(bookDao);
    }

    @Test
    void getBookByIdSuccessTest() {
        String bookId = "book-id";

        Mockito.when(bookDao.getById(bookId)).thenReturn(new Book(bookId));

        Book bookFromDB = bookService.getById(bookId);

        assertEquals(
                bookId,
                bookFromDB.getBookId()
        );
    }

    @Test
    void getBookByIdBadIdExceptionTest() {
        assertThrows(
                BadIdException.class,
                () -> bookService.getById("       ")
        );
    }

    @Test
    void getValidatedBookNameExpectBookNameIsNullExceptionTest() {
        assertThrows(
                BookNameIsNullException.class,
                () -> bookService.getValidatedBookName(null)
        );
    }

    @Test
    void getValidatedBookNameExpectBookNameIsTooLongExceptionTest() {
        StringBuilder str = new StringBuilder(
                "Lorem ipsum dolor sit amet, " +
                        "consectetur adipiscing elit. Vivamus at"
        );
        str.append(String.valueOf(str).repeat(20));
        assertThrows(
                BookNameIsTooLongException.class,
                () -> bookService.getValidatedBookName(str.toString())
        );
    }

    @Test
    void getValidatedBookNameTest() {
        String str = "this is some book name";
        assertEquals(str, bookService.getValidatedBookName(str));
    }

    @Test
    void getCreatedBookTest() {
        String name = "the some book name";
        String desc = "the some book desc";
        int numberOfWords = 154322;
        int rating = 8;
        int year = 2012;
        List<String> authors = Arrays.asList("Some author name 1", "Some author name 2");

        CreateBookDto bookDto = new CreateBookDto();
        bookDto.setName(name);
        bookDto.setDescription(desc);
        bookDto.setNumberOfWords(numberOfWords);
        bookDto.setRating(rating);
        bookDto.setYearOfPublication(year);
        bookDto.setAuthors(authors);

        assertNotNull(bookDto);

        assertEquals(name, bookDto.getName());
        assertEquals(desc, bookDto.getDescription());
        assertEquals(numberOfWords, bookDto.getNumberOfWords());
        assertEquals(rating, bookDto.getRating());
        assertEquals(year, bookDto.getYearOfPublication());
        assertIterableEquals(authors, bookDto.getAuthors());

        Mockito.when(bookService.createBook(bookDto)).thenReturn(null);

    }
}
