package fh.swen.swen2tourplanner.persistence;

import fh.swen.swen2tourplanner.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}
