package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.repository.ApprentiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j

@Service
public class ApprentiService {

    private final ApprentiRepository apprentiRepository;

    public ApprentiService(ApprentiRepository apprentiRepository) {
        this.apprentiRepository = apprentiRepository;
    }

    public List<Apprenti> getAllApprentis() {
        return apprentiRepository.findAll();
    }

    public List<Apprenti> getAllApprentisNoArchived() {
        return apprentiRepository.findAll().stream().filter(apprenti-> !apprenti.isArchived()).toList();
    }

    public Apprenti getApprenti(Long id) {
        return apprentiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apprenti non trouvé"));
    }

    @Transactional
    public Apprenti createOrUpdateApprenti(Apprenti apprenti) {
        log.info("update " + apprenti);
        return apprentiRepository.save(apprenti);
    }

    public void deleteApprenti(Long id) {
        apprentiRepository.deleteById(id);
    }

    public List<Apprenti> searchByNom(String nom) {
        return apprentiRepository.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void newAcademiqueYear() {
        log.info("new Year");
        getAllApprentis().forEach(apprenti -> {
            apprenti.addYear();
            apprentiRepository.save(apprenti);
        });
    }


    public void archive(Long id){
        getApprenti(id).setArchived(true);
    }


}