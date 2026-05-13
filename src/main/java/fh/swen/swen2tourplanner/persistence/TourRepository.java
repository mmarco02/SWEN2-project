package fh.swen.swen2tourplanner.persistence;

import fh.swen.swen2tourplanner.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByUserId(Long userId);
    List<Tour> findTourByFromLocationAndToLocation(String fromLocation, String toLocation);

    List<Tour> findTourByUserId(Long userId);
}
