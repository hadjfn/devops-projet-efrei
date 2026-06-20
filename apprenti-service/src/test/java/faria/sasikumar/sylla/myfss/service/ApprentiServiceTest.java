package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.repository.ApprentiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprentiServiceTest {

    @Mock
    private ApprentiRepository repository;

    @InjectMocks
    private ApprentiService service;

    private Apprenti alice;
    private Apprenti bob;
    private Apprenti archivedCarol;

    @BeforeEach
    void setUp() {
        alice = new Apprenti("Doe", "Alice", "alice@efrei.fr", "0102030405",
                "BSc", "DA", 1);
        alice.setId(1L);

        bob = new Apprenti("Smith", "Bob", "bob@efrei.fr", "0102030406",
                "MSc", "BI", 3);
        bob.setId(2L);

        archivedCarol = new Apprenti("Old", "Carol", "carol@efrei.fr",
                "0102030407", "BSc", "DA", 4);
        archivedCarol.setId(3L);
        archivedCarol.setArchived(true);
    }

    @Test
    void getAllApprentis_returnsRepositoryContent() {
        when(repository.findAll()).thenReturn(List.of(alice, bob, archivedCarol));

        assertThat(service.getAllApprentis()).hasSize(3);
    }

    @Test
    void getAllApprentisNoArchived_filtersOutArchived() {
        when(repository.findAll()).thenReturn(List.of(alice, bob, archivedCarol));

        List<Apprenti> result = service.getAllApprentisNoArchived();

        assertThat(result).hasSize(2).extracting(Apprenti::getPrenom)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void getApprenti_returnsEntityWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));

        Apprenti found = service.getApprenti(1L);

        assertThat(found.getPrenom()).isEqualTo("Alice");
    }

    @Test
    void getApprenti_throwsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getApprenti(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non trouvé");
    }

    @Test
    void createOrUpdateApprenti_delegatesToRepository() {
        when(repository.save(alice)).thenReturn(alice);

        Apprenti saved = service.createOrUpdateApprenti(alice);

        assertThat(saved).isSameAs(alice);
        verify(repository).save(alice);
    }

    @Test
    void deleteApprenti_callsRepository() {
        service.deleteApprenti(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void searchByNom_delegatesToRepository() {
        when(repository.findByNomContainingIgnoreCase("doe"))
                .thenReturn(List.of(alice));

        assertThat(service.searchByNom("doe")).containsExactly(alice);
    }

    @Test
    void newAcademiqueYear_incrementsYearAndArchivesOver3() {
        when(repository.findAll()).thenReturn(List.of(alice, bob));
        when(repository.save(any(Apprenti.class))).thenAnswer(i -> i.getArgument(0));

        service.newAcademiqueYear();

        assertThat(alice.getAnnee()).isEqualTo(2);
        assertThat(alice.isArchived()).isFalse();
        assertThat(bob.getAnnee()).isEqualTo(4);
        assertThat(bob.isArchived()).isTrue();
        verify(repository, times(2)).save(any(Apprenti.class));
    }

    @Test
    void archive_setsArchivedFlagToTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));

        service.archive(1L);

        assertThat(alice.isArchived()).isTrue();
    }
}
