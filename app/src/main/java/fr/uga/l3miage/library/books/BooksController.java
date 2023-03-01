package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class BooksController {

    private final BookService bookService;
    private final BooksMapper booksMapper;

    @Autowired
    public BooksController(BookService bookService, BooksMapper booksMapper) {
       this.bookService = bookService;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/books")
    public Collection<BookDTO> books(@RequestParam(value = "q", required = false) String query) {
        return bookService.list().stream()
                        .map(booksMapper::entityToDTO)
                        .toList();
    }

    @GetMapping("/books/{id}")
    public BookDTO book(@PathVariable("id") Long id) {
        try {
            Book book = bookService.get(id);
            return booksMapper.entityToDTO(book);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The book was not found");
        }
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO newBook(Long authorId, BookDTO book) {
        Book newBook = booksMapper.dtoToEntity(book);
        try {
            Book saved = bookService.save(authorId, newBook);
            return booksMapper.entityToDTO(saved);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The author of the book doesn't exist");
        }
    }

    public BookDTO updateBook(Long authorId, BookDTO book) {
        // attention BookDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        return null;
    }

    public void deleteBook(Long id) {

    }

    public void addAuthor(Long authorId, AuthorDTO author) {

    }
}
