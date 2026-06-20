package faria.sasikumar.sylla.myfss.repository;

import faria.sasikumar.sylla.myfss.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {}
