package com.example.spring_practice.controller;

import com.example.spring_practice.dto.BookDto;
import com.example.spring_practice.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String showBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/list";
    }

    @GetMapping("/{id}")
    public String showBookDetail(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/detail";
    }

    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookDto", new BookDto());
        return "books/form";
    }

    @PostMapping
    public String saveBook(@ModelAttribute BookDto bookDto) {
        bookService.createBook(bookDto);
        return "redirect:/books";
    }
}
