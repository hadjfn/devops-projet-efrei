package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.exception.NotFoundException;
import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.repository.ApprentiRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(ApprentiService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ApprentiPersistenceTest {
    @Autowired private ApprentiService service;
    @Autowired private ApprentiRepository repository;

    @AfterEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private Apprenti apprentice() {
        return new Apprenti("Doe", "Alice", "alice@example.org", "0102030405", "BSc", "DA", 1);
    }

    @Test
    void archive_isCommittedAndVisibleInANewPersistenceContext() {
        Long id = repository.save(apprentice()).getId();
        service.archive(id);
        assertThat(repository.findById(id).orElseThrow().isArchived()).isTrue();
        assertThat(repository.findByArchivedFalse()).isEmpty();
    }

    @Test
    void editingAnArchivedApprentice_preservesTheArchiveFlag() {
        Apprenti persisted = apprentice();
        persisted.setArchived(true);
        Long id = repository.save(persisted).getId();
        Apprenti form = apprentice();
        form.setId(id);
        form.setPrenom("Updated");
        service.createOrUpdateApprenti(form);
        Apprenti reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.isArchived()).isTrue();
        assertThat(reloaded.getPrenom()).isEqualTo("Updated");
    }

    @Test
    void newRecord_cannotSetArchiveStateThroughTheEditForm() {
        Apprenti form = apprentice();
        form.setArchived(true);
        Long id = service.createOrUpdateApprenti(form).getId();
        assertThat(repository.findById(id).orElseThrow().isArchived()).isFalse();
    }

    @Test
    void editingMissingRecord_doesNotCreateAnUnexpectedRow() {
        Apprenti form = apprentice();
        form.setId(999L);
        assertThatThrownBy(() -> service.createOrUpdateApprenti(form)).isInstanceOf(NotFoundException.class);
        assertThat(repository.count()).isZero();
    }

    @Test
    void academicYear_changesAreCommittedTogether() {
        Apprenti finalYear = apprentice();
        finalYear.setAnnee(3);
        Long id = repository.save(finalYear).getId();
        service.newAcademiqueYear();
        Apprenti reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.getAnnee()).isEqualTo(4);
        assertThat(reloaded.isArchived()).isTrue();
    }
}
