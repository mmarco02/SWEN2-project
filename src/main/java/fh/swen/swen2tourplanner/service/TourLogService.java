package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourLogMapper tourLogMapper;

    public List<TourLog> getByTourId(Long tourId) {
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog create(TourLogDTO dto) {
        Tour tour = tourRepository.findById(dto.tourId())
                .orElseThrow(() -> new IllegalArgumentException("Tour not found"));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TourLog tourLog = tourLogMapper.mapToEntity(dto);
        tourLog.setTour(tour);
        tourLog.setUser(user);
        return tourLogRepository.save(tourLog);
    }

    public TourLog update(Long id, TourLogDTO dto) {
        TourLog existing = tourLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour log not found"));

        existing.setDateTime(dto.dateTime());
        existing.setComment(dto.comment());
        existing.setDifficulty(dto.difficulty());
        existing.setTotalDistance(dto.totalDistance());
        existing.setTotalTime(dto.totalTime());
        existing.setRating(dto.rating());
        return tourLogRepository.save(existing);
    }

    public void delete(Long id) {
        tourLogRepository.deleteById(id);
    }
}
