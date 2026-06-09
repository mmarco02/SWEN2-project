package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {
    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final UserRepository userRepository;
    private final TourMapper tourMapper;
    private final TourImageService tourImageService;

    public List<TourDTO> getAll() {
        log.debug("Fetching all tours");
        return tourMapper.mapToDTOList(tourRepository.findAll());
    }

    public TourDTO getById(Long id) {
        log.debug("Fetching tour: id={}", id);
        return tourMapper.mapToDTO(findTourOrThrow(id));
    }

    @Transactional
    public TourDTO createTour(TourDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", dto.userId());
                    return new ResourceNotFoundException("User", dto.userId());
                });

        Tour tour = tourMapper.mapToEntity(dto);
        tour.setUser(user);

        Tour saved = tourRepository.save(tour);
        log.info("Tour created: id={}", saved.getId());
        return tourMapper.mapToDTO(saved);
    }

    public List<TourDTO> getToursByUserId(Long userId) {
        log.debug("Fetching tours for userId={}", userId);
        return tourMapper.mapToDTOList(tourRepository.findTourByUserId(userId));
    }

    public List<TourDTO> getToursFromTo(String from, String to) {
        log.debug("Fetching tours from={} to={}", from, to);
        return tourMapper.mapToDTOList(tourRepository.findTourByFromLocationAndToLocation(from, to));
    }

    @Transactional
    public TourDTO updateTour(Long id, TourDTO dto) {
        Tour existing = findTourOrThrow(id);
        tourMapper.updateEntity(existing, dto);

        Tour saved = tourRepository.save(existing);
        log.info("Tour updated: id={}", saved.getId());
        return tourMapper.mapToDTO(saved);
    }

    @Transactional
    public void deleteTour(Long id) {
        Tour tour = findTourOrThrow(id);
        tourImageService.deleteImageForTour(tour);
        tourRepository.deleteById(id);
        log.info("Tour deleted: id={}", id);
    }

    @Transactional
    public void recalculateComputedAttributes(Long tourId) {
        Tour tour = findTourOrThrow(tourId);
        List<TourLog> logs = tourLogRepository.findByTourId(tourId);

        if (logs.isEmpty()) {
            tour.setPopularity(null);
            tour.setChildFriendliness(null);
        } else {
            double avgRating = logs.stream().mapToInt(TourLog::getRating).average().orElse(0);
            tour.setPopularity(avgRating >= 4.0 ? "POPULAR" : avgRating >= 2.5 ? "AVERAGE" : "UNPOPULAR");

            double avgDifficulty = logs.stream()
                    .mapToInt(this::difficultyToInt)
                    .average().orElse(0);
            double avgDistance = logs.stream()
                    .mapToDouble(TourLog::getTotalDistance)
                    .average().orElse(0);

            if (avgDifficulty <= 1.5 && avgDistance <= 10) {
                tour.setChildFriendliness("CHILD_FRIENDLY");
            } else if (avgDifficulty <= 2.5 && avgDistance <= 25) {
                tour.setChildFriendliness("MODERATE");
            } else {
                tour.setChildFriendliness("NOT_CHILD_FRIENDLY");
            }
        }

        tourRepository.save(tour);
        log.debug("Recalculated computed attributes for tour id={}", tourId);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(1)
    public void recalculateAllOnStartup() {
        log.info("Recalculating computed attributes for all tours");
        List<Tour> allTours = tourRepository.findAll();
        for (Tour tour : allTours) {
            List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());
            if (logs.isEmpty()) {
                tour.setPopularity(null);
                tour.setChildFriendliness(null);
            } else {
                double avgRating = logs.stream().mapToInt(TourLog::getRating).average().orElse(0);
                tour.setPopularity(avgRating >= 4.0 ? "POPULAR" : avgRating >= 2.5 ? "AVERAGE" : "UNPOPULAR");

                double avgDifficulty = logs.stream()
                        .mapToInt(this::difficultyToInt)
                        .average().orElse(0);
                double avgDistance = logs.stream()
                        .mapToDouble(TourLog::getTotalDistance)
                        .average().orElse(0);

                if (avgDifficulty <= 1.5 && avgDistance <= 10) {
                    tour.setChildFriendliness("CHILD_FRIENDLY");
                } else if (avgDifficulty <= 2.5 && avgDistance <= 25) {
                    tour.setChildFriendliness("MODERATE");
                } else {
                    tour.setChildFriendliness("NOT_CHILD_FRIENDLY");
                }
            }
            tourRepository.save(tour);
        }
    }

    private int difficultyToInt(TourLog log) {
        return switch (log.getDifficulty()) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
            case EXPERT -> 4;
        };
    }

    private Tour findTourOrThrow(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tour not found: id={}", id);
                    return new ResourceNotFoundException("Tour", id);
                });
    }
}
