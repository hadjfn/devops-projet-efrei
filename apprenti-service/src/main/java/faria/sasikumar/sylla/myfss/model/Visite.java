package faria.sasikumar.sylla.myfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
public class Visite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String format;
    private String commentaires;

    @ManyToOne
    private Apprenti apprenti;

}
