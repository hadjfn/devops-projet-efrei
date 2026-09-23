package faria.sasikumar.sylla.myfss.service;

import faria.sasikumar.sylla.myfss.model.Apprenti;
import faria.sasikumar.sylla.myfss.exception.NotFoundException;
import faria.sasikumar.sylla.myfss.repository.ApprentiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ApprentiService {

    private final ApprentiRepository apprentiRepository;

    public ApprentiService(ApprentiRepository apprentiRepository) {
        this.apprentiRepository = apprentiRepository;
    }

    public List<Apprenti> getAllApprentis() {
        return apprentiRepository.findAll();
    }

    public List<Apprenti> getAllApprentisNoArchived() {
        return apprentiRepository.findByArchivedFalse();
    }

    public Apprenti getApprenti(Long id) {
        return apprentiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Apprenti non trouvé"));
    }

    @Transactional
    public Apprenti createOrUpdateApprenti(Apprenti apprenti) {
        // Apply editable fields to a managed entity. A form cannot archive a record
        // or accidentally restore one when its archive flag is not submitted.
        Apprenti target = apprenti.getId() == null ? new Apprenti() : getApprenti(apprenti.getId());
        target.setNom(apprenti.getNom());
        target.setPrenom(apprenti.getPrenom());
        target.setEmail(apprenti.getEmail());
        target.setTelephone(apprenti.getTelephone());
        target.setProgramme(apprenti.getProgramme());
        target.setMajeure(apprenti.getMajeure());
        target.setAnnee(apprenti.getAnnee());
        return apprentiRepository.save(target);
    }

    @Transactional
    public void deleteApprenti(Long id) {
        apprentiRepository.delete(getApprenti(id));
    }

    public List<Apprenti> searchByNom(String nom) {
        return apprentiRepository.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void newAcademiqueYear() {
        getAllApprentis().forEach(apprenti -> {
            apprenti.addYear();
            apprentiRepository.save(apprenti);
        });
    }


    @Transactional
    public void archive(Long id){
        getApprenti(id).setArchived(true);
    }


}
