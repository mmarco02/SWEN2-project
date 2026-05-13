package fh.swen.swen2tourplanner.persistence;

import fh.swen.swen2tourplanner.domain.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TourLogRepository extends JpaRepository<TourLog, Long> {

}
