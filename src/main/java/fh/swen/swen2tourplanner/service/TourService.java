package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private static final Logger logger = LogManager.getLogger(TourService.class);

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public List<Tour> getAll() {
        logger.debug("Fetching all tours");
        return tourRepository.findAll();
    }

    public Tour createTour(TourDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> {
                    logger.warn("User not found: id={}", dto.userId());
                    return new IllegalArgumentException("User not found");
                });

        Tour tour = dto.toEntity();
        tour.setUser(user);

        Tour saved = tourRepository.save(tour);
        logger.info("Tour created: id={}", saved.getId());
        return saved;
    }

    public List<Tour> getToursByUserId(Long userId) {
        logger.debug("Fetching tours for userId={}", userId);
        return tourRepository.findTourByUserId(userId);
    }

    public List<Tour> getToursFromTo(String from, String to) {
        logger.debug("Fetching tours from={} to={}", from, to);
        return tourRepository.findTourByFromLocationAndToLocation(from, to);
    }

    public Tour updateTour(Long id, TourDTO dto) {
        Tour existing = tourRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tour not found: id={}", id);
                    return new IllegalArgumentException("Tour not found");
                });

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setFromLocation(dto.fromLocation());
        existing.setToLocation(dto.toLocation());
        existing.setTransportType(dto.transportType());
        existing.setDistanceKm(dto.distanceKm());
        existing.setEstimatedTime(dto.estimatedTime());
        existing.setRoute(dto.route());

        Tour saved = tourRepository.save(existing);
        logger.info("Tour updated: id={}", saved.getId());
        return saved;
    }

    public void deleteTour(Long id) {
        tourRepository.deleteById(id);
        logger.info("Tour deleted: id={}", id);
    }
}
