package faria.sasikumar.sylla.myfss.repository;

import faria.sasikumar.sylla.myfss.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {}
