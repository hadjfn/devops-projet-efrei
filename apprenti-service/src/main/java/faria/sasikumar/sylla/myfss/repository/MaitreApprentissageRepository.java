package faria.sasikumar.sylla.myfss.repository;

import faria.sasikumar.sylla.myfss.model.MaitreApprentissage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaitreApprentissageRepository extends JpaRepository<MaitreApprentissage, Long> {}
