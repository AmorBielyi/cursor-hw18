package com.cursor.library.controllers;

import com.cursor.library.daos.BookDao;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerTest extends BaseControllerTest {

    private BookDao bookDao;

    @BeforeAll
    void setUp() {
        bookDao = new BookDao();
    }

    @Test
    @Order(1)
    void getAllTest() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Book> books = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        List<Book> booksFromDb = bookDao.getAll();
        assertIterableEquals(booksFromDb, books);
    }

    @Test
    @Order(2)
    void deleteByIdSuccessTest() throws Exception {
        String bookId = "random_id_value_1";

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(3)
    void deleteByIdExceptionNotFoundStatusTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/book/{bookId}", "someId"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(4)
    void getByIdSuccessTest() throws Exception {
        Book bookFromDao = bookDao.getById("random_id_value_5");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookFromDao.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Book book = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                Book.class
        );
        assertEquals(bookFromDao, book);
    }

    @Test
    @Order(5)
    void getByIdExceptionNotFoundStatusTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books/{bookId}", "some id");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(6)
    public void createBookTest() throws Exception {
        CreateBookDto createBookDto = new CreateBookDto();
        createBookDto.setName("Cool createBookDto");
        createBookDto.setDescription("Cool description");
        createBookDto.setNumberOfWords(100500);
        createBookDto.setRating(10);
        createBookDto.setYearOfPublication(2020);
        createBookDto.setAuthors(Arrays.asList("author1", "author2"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(OBJECT_MAPPER.writeValueAsString(createBookDto));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Book book = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                Book.class
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
