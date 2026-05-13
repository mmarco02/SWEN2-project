package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public List<Tour> getAll() {
        return tourRepository.findAll();
    }

    public List<Tour> getByUserId(Long userId) {
        return tourRepository.findByUserId(userId);
    }

    public Tour createTour(TourDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Tour tour = dto.toEntity();
        tour.setUser(user);
        return tourRepository.save(tour);
    }

    public List<Tour> getToursByUserId(Long userId) {
        return tourRepository.findTourByUserId(userId);
    }

    public List<Tour> getToursFromTo(String from, String to) {
        return tourRepository.findTourByFromLocationAndToLocation(from, to);
    }

    public Tour updateTour(Long id, TourDTO dto) {
        Tour existing = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setFromLocation(dto.fromLocation());
        existing.setToLocation(dto.toLocation());
        existing.setTransportType(dto.transportType());
        existing.setDistanceKm(dto.distanceKm());
        existing.setEstimatedTime(dto.estimatedTime());
        existing.setRoute(dto.route());
        return tourRepository.save(existing);
    }

    public void deleteTour(Long id) {
        tourRepository.deleteById(id);
    }
}
