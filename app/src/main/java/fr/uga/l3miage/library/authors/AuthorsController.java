package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;
    private final BookService bookService;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper, BookService bookService) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
        this.bookService = bookService;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }
    
    @GetMapping("/authors/{id}")
    public AuthorDTO author(@PathVariable("id") Long id) {
        try {
            Author auteur = authorService.get(id);
            return authorMapper.entityToDTO(auteur);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The author was not found");
        }
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody @Valid AuthorDTO author) {
        Author auteur = authorMapper.dtoToEntity(author);
        Author newAuthor = authorService.save(auteur);
        return authorMapper.entityToDTO(newAuthor);
    }
    
    @PutMapping("/authors/{id}")
    public AuthorDTO updateAuthor(@PathVariable("id") Long id, @RequestBody @Valid AuthorDTO author) throws EntityNotFoundException {
        // attention AuthorDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        if(author.id() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The author was not found");
        } 
        Author auteur = authorMapper.dtoToEntity(author);
        Author updated = authorService.update(auteur);
        return authorMapper.entityToDTO(updated);
    }

    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  //code HTTP 204
    public void deleteAuthor(@PathVariable("id") Long id) {
        try{
            authorService.delete(id);
        }catch(DeleteAuthorException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot delete author, one or several books are co-authored");
        }catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The author doesn't exist");
        }
    }

    //return all books of an author
    @GetMapping("/authors/{id}/books")
    public Collection<BookDTO> books(@PathVariable("id") Long authorId) {
        try{
            Collection<Book> books = bookService.getByAuthor(authorId);
            return books.stream()
                        .map(booksMapper::entityToDTO)
                        .toList();
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The author was not found");
        }
    }

    //create a new book for an author
    @PostMapping("/authors/{id}/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO newBook(@PathVariable("id") Long authorId, @RequestBody @Valid BookDTO bookDTO) {
        Book book = booksMapper.dtoToEntity(bookDTO);
        try {
            Book saved = bookService.save(authorId, book);
            return booksMapper.entityToDTO(saved);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The author was not found");
        }
    }

}