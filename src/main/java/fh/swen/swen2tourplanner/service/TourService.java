package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {
    private final TourRepository tourRepository;
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

    private Tour findTourOrThrow(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tour not found: id={}", id);
                    return new ResourceNotFoundException("Tour", id);
                });
    }
}
