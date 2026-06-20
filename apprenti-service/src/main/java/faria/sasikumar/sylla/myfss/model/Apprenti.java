package faria.sasikumar.sylla.myfss.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Apprenti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Le téléphone doit contenir 10 chiffres")
    private String telephone;

    private String programme;
    private String majeure;
    private int annee;
    private boolean archived;

    public Apprenti() {
    }

    public Apprenti(String nom, String prenom, String email, String telephone, String programme, String majeure, int annee) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.programme = programme;
        this.majeure = majeure;
        this.annee = annee;
    }

    public void addYear(){
        this.annee++;
        if(annee > 3){
            this.archived=true;
        }
    }

}