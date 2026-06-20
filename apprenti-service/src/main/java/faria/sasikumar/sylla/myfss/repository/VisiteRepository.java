package faria.sasikumar.sylla.myfss.repository;

import faria.sasikumar.sylla.myfss.model.Visite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisiteRepository extends JpaRepository<Visite, Long> {}
