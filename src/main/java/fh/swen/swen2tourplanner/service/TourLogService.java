package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourLogMapper tourLogMapper;

    public List<TourLogDTO> getByTourId(Long tourId) {
        return tourLogMapper.mapToDTOList(tourLogRepository.findByTourId(tourId));
    }

    @Transactional
    public TourLogDTO create(Long tourId, TourLogDTO dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.userId()));

        TourLog tourLog = tourLogMapper.mapToEntity(dto);
        tourLog.setTour(tour);
        tourLog.setUser(user);
        return tourLogMapper.mapToDTO(tourLogRepository.save(tourLog));
    }

    @Transactional
    public TourLogDTO update(Long id, TourLogDTO dto) {
        TourLog existing = tourLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TourLog", id));

        tourLogMapper.updateEntity(existing, dto);
        return tourLogMapper.mapToDTO(tourLogRepository.save(existing));
    }

    public void delete(Long id) {
        if (!tourLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("TourLog", id);
        }
        tourLogRepository.deleteById(id);
    }
}
