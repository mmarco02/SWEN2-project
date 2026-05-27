package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourMapper tourMapper;
    private final ImageStorageService imageStorageService;

    public List<Tour> getAll() {
        log.debug("Fetching all tours");
        return tourRepository.findAll();
    }

    public Tour getById(Long id) {
        log.debug("Fetching tour: id={}", id);
        return tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tour not found: id={}", id);
                    return new IllegalArgumentException("Tour not found");
                });
    }

    public Tour createTour(TourDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", dto.userId());
                    return new IllegalArgumentException("User not found");
                });

        Tour tour = tourMapper.mapToEntity(dto);
        tour.setUser(user);

        Tour saved = tourRepository.save(tour);
        log.info("Tour created: id={}", saved.getId());
        return saved;
    }

    public List<Tour> getToursByUserId(Long userId) {
        log.debug("Fetching tours for userId={}", userId);
        return tourRepository.findTourByUserId(userId);
    }

    public List<Tour> getToursFromTo(String from, String to) {
        log.debug("Fetching tours from={} to={}", from, to);
        return tourRepository.findTourByFromLocationAndToLocation(from, to);
    }

    public Tour updateTour(Long id, TourDTO dto) {
        Tour existing = tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tour not found: id={}", id);
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
        existing.setImagePath(dto.imagePath());

        Tour saved = tourRepository.save(existing);
        log.info("Tour updated: id={}", saved.getId());
        return saved;
    }

    public TourDTO uploadImage(Long tourId, MultipartFile file) throws IOException {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));

        if (tour.getImagePath() != null) {
            imageStorageService.delete(tour.getImagePath());
        }

        String filename = imageStorageService.store(file);
        tour.setImagePath(filename);
        Tour saved = tourRepository.save(tour);
        log.info("Image uploaded for tour: id={}", tourId);
        return tourMapper.mapToDTO(saved);
    }

    public void deleteTour(Long id) {
        Tour tour = tourRepository.findById(id).orElse(null);
        if (tour != null && tour.getImagePath() != null) {
            try {
                imageStorageService.delete(tour.getImagePath());
            } catch (IOException e) {
                log.warn("Failed to delete image for tour: id={}", id);
            }
        }
        tourRepository.deleteById(id);
        log.info("Tour deleted: id={}", id);
    }
}
