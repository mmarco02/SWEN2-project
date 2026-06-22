package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.Difficulty;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourLogServiceTest {

    @Mock private TourLogRepository tourLogRepository;
    @Mock private TourRepository tourRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourLogMapper tourLogMapper;
    @Mock private TourService tourService;

    @InjectMocks
    private TourLogService tourLogService;

    private User testUser;
    private Tour testTour;
    private TourLog testLog;
    private TourLogDTO testLogDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTour = Tour.builder()
                .id(1L)
                .name("Test Tour")
                .description("desc")
                .fromLocation("Vienna")
                .toLocation("Graz")
                .transportType(TransportType.CAR)
                .user(testUser)
                .build();

        testLog = TourLog.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 6, 1, 10, 0))
                .comment("Nice tour")
                .difficulty(Difficulty.EASY)
                .totalDistance(15.0)
                .totalTime(60)
                .rating(4)
                .tour(testTour)
                .user(testUser)
                .build();

        testLogDTO = new TourLogDTO(1L, LocalDateTime.of(2026, 6, 1, 10, 0),
                "Nice tour", Difficulty.EASY, 15.0, 60, 4, 1L, 1L);
    }

    @Test
    void getByTourId_returnsLogs() {
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(testLog));
        when(tourLogMapper.mapToDTOList(List.of(testLog))).thenReturn(List.of(testLogDTO));

        List<TourLogDTO> result = tourLogService.getByTourId(1L);

        assertEquals(1, result.size());
        assertEquals("Nice tour", result.get(0).comment());
    }

    @Test
    void create_validInput_createsLogAndRecalculates() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tourLogMapper.mapToEntity(testLogDTO)).thenReturn(testLog);
        when(tourLogRepository.save(any(TourLog.class))).thenReturn(testLog);
        when(tourLogMapper.mapToDTO(testLog)).thenReturn(testLogDTO);

        TourLogDTO result = tourLogService.create(1L, testLogDTO);

        assertNotNull(result);
        verify(tourService).recalculateComputedAttributes(1L);
    }

    @Test
    void create_tourNotFound_throwsException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourLogService.create(99L, testLogDTO));
    }

    @Test
    void delete_existingLog_deletesAndRecalculates() {
        when(tourLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        tourLogService.delete(1L);

        verify(tourLogRepository).deleteById(1L);
        verify(tourService).recalculateComputedAttributes(1L);
    }

    @Test
    void delete_nonExistingLog_throwsException() {
        when(tourLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourLogService.delete(99L));
    }
}
