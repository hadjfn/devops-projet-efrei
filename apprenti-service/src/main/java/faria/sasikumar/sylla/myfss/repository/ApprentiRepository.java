package faria.sasikumar.sylla.myfss.repository;
import faria.sasikumar.sylla.myfss.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprentiRepository extends JpaRepository<Apprenti, Long> {
    List<Apprenti> findByNomContainingIgnoreCase(String nom);
}