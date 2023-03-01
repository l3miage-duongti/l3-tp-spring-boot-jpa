package fr.uga.l3miage.library.books;

import fr.uga.l3miage.library.authors.AuthorDTO;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Collection;

public record BookDTO(
        Long id,
        @NotEmpty(message = "title cannot be empty")
        String title,

        @Min(1000000000) 
        //@Max(99999999999999)
        long isbn,

        String publisher,

        @NotNull @Min(-9999) @Max(9999) 
        short year,

        @NotEmpty @Pattern(regexp="^(english|french)", message = "wrong language")
        String language,

        Collection<AuthorDTO> authors
) {
}
