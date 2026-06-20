package faria.sasikumar.sylla.myfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memoire;
    private String theme;
    private double noteFinaleMemoire;
    private double noteFinaleSoutenance;
    private String commentaires;

    @OneToOne
    private Apprenti apprenti;

}
