package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.Difficulty;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock private TourRepository tourRepository;
    @Mock private TourLogRepository tourLogRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourMapper tourMapper;
    @Mock private TourImageService tourImageService;

    @InjectMocks
    private TourService tourService;

    private User testUser;
    private Tour testTour;
    private TourDTO testTourDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTour = Tour.builder()
                .id(1L)
                .name("Test Tour")
                .description("A test tour")
                .fromLocation("Vienna")
                .toLocation("Graz")
                .transportType(TransportType.CAR)
                .distanceKm(200.0)
                .estimatedTime(120)
                .user(testUser)
                .build();

        testTourDTO = new TourDTO(1L, "Test Tour", "A test tour", "Vienna", "Graz",
                TransportType.CAR, 200.0, 120, null, null, 1L, "testuser", null, null);
    }

    @Test
    void getById_existingTour_returnsTourDTO() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourMapper.mapToDTO(testTour)).thenReturn(testTourDTO);

        TourDTO result = tourService.getById(1L);

        assertEquals("Test Tour", result.name());
        verify(tourRepository).findById(1L);
    }

    @Test
    void getById_nonExistingTour_throwsResourceNotFoundException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.getById(99L));
    }

    @Test
    void createTour_validInput_returnsSavedTour() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tourMapper.mapToEntity(testTourDTO)).thenReturn(testTour);
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);
        when(tourMapper.mapToDTO(testTour)).thenReturn(testTourDTO);

        TourDTO result = tourService.createTour(testTourDTO);

        assertNotNull(result);
        assertEquals("Test Tour", result.name());
        verify(tourRepository).save(any(Tour.class));
    }

    @Test
    void createTour_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.createTour(testTourDTO));
    }

    @Test
    void deleteTour_existingTour_deletesSuccessfully() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));

        tourService.deleteTour(1L);

        verify(tourImageService).deleteImageForTour(testTour);
        verify(tourRepository).deleteById(1L);
    }

    @Test
    void deleteTour_nonExistingTour_throwsException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.deleteTour(99L));
    }

    @Test
    void updateTour_existingTour_returnsUpdatedTour() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);
        when(tourMapper.mapToDTO(testTour)).thenReturn(testTourDTO);

        TourDTO result = tourService.updateTour(1L, testTourDTO);

        assertNotNull(result);
        verify(tourMapper).updateEntity(testTour, testTourDTO);
        verify(tourRepository).save(testTour);
    }

    @Test
    void recalculateComputedAttributes_noLogs_setsNull() {
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(Collections.emptyList());
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        tourService.recalculateComputedAttributes(1L);

        assertNull(testTour.getPopularity());
        assertNull(testTour.getChildFriendliness());
    }

    @Test
    void recalculateComputedAttributes_highRatings_setsPopularAndChildFriendly() {
        TourLog log1 = TourLog.builder().rating(5).difficulty(Difficulty.EASY).totalDistance(5.0).build();
        TourLog log2 = TourLog.builder().rating(4).difficulty(Difficulty.EASY).totalDistance(8.0).build();

        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log1, log2));
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        tourService.recalculateComputedAttributes(1L);

        assertEquals("POPULAR", testTour.getPopularity());
        assertEquals("CHILD_FRIENDLY", testTour.getChildFriendliness());
    }

    @Test
    void recalculateComputedAttributes_lowRatings_setsUnpopularAndNotChildFriendly() {
        TourLog log1 = TourLog.builder().rating(1).difficulty(Difficulty.EXPERT).totalDistance(50.0).build();
        TourLog log2 = TourLog.builder().rating(2).difficulty(Difficulty.HARD).totalDistance(40.0).build();

        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log1, log2));
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        tourService.recalculateComputedAttributes(1L);

        assertEquals("UNPOPULAR", testTour.getPopularity());
        assertEquals("NOT_CHILD_FRIENDLY", testTour.getChildFriendliness());
    }

    @Test
    void recalculateComputedAttributes_moderateValues_setsAverageAndModerate() {
        TourLog log1 = TourLog.builder().rating(3).difficulty(Difficulty.MEDIUM).totalDistance(15.0).build();
        TourLog log2 = TourLog.builder().rating(3).difficulty(Difficulty.EASY).totalDistance(20.0).build();

        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log1, log2));
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        tourService.recalculateComputedAttributes(1L);

        assertEquals("AVERAGE", testTour.getPopularity());
        assertEquals("MODERATE", testTour.getChildFriendliness());
    }
}
