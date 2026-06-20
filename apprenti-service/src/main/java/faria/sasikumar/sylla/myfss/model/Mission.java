package faria.sasikumar.sylla.myfss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String motsCles;
    private String metierCible;
    private String commentaires;

    @ManyToOne
    private Apprenti apprenti;

}
