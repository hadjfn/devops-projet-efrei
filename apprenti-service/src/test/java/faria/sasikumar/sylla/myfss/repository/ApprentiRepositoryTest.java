package faria.sasikumar.sylla.myfss.repository;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ApprentiRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ApprentiRepository repository;

    @Test
    void saveAndFind_persistsApprenti() {
        Apprenti a = new Apprenti("Dupont", "Marie", "marie@efrei.fr",
                "0102030405", "BSc", "DA", 1);

        Apprenti saved = repository.save(a);

        assertThat(saved.getId()).isNotNull();
        assertThat(em.find(Apprenti.class, saved.getId()).getPrenom()).isEqualTo("Marie");
    }

    @Test
    void findByNomContainingIgnoreCase_matchesPartialAndCaseInsensitive() {
        em.persist(new Apprenti("Dupont",  "Marie", "m@e.fr", "0102030405", "BSc", "DA", 1));
        em.persist(new Apprenti("DUPONTEL", "Jean",  "j@e.fr", "0102030406", "MSc", "BI", 2));
        em.persist(new Apprenti("Martin",  "Lou",   "l@e.fr", "0102030407", "BSc", "DA", 1));
        em.flush();

        List<Apprenti> result = repository.findByNomContainingIgnoreCase("dupont");

        assertThat(result).hasSize(2).extracting(Apprenti::getNom)
                .containsExactlyInAnyOrder("Dupont", "DUPONTEL");
    }

    @Test
    void findAll_returnsAllSavedApprentis() {
        em.persist(new Apprenti("A", "A", "a@e.fr", "0102030405", "BSc", "DA", 1));
        em.persist(new Apprenti("B", "B", "b@e.fr", "0102030406", "BSc", "DA", 2));
        em.flush();

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void deleteById_removesEntity() {
        Apprenti a = em.persist(new Apprenti("Z", "Z", "z@e.fr", "0102030405", "BSc", "DA", 1));
        em.flush();

        repository.deleteById(a.getId());

        assertThat(repository.findById(a.getId())).isEmpty();
    }
}
