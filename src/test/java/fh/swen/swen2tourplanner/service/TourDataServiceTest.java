package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.Difficulty;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourDataServiceTest {

    @Mock private TourRepository tourRepository;
    @Mock private TourLogRepository tourLogRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourService tourService;

    @InjectMocks
    private TourDataService tourDataService;

    @Test
    void exportToCsv_noTours_returnsHeaderOnly() {
        when(tourRepository.findTourByUserId(1L)).thenReturn(Collections.emptyList());

        String csv = tourDataService.exportToCsv(1L);

        assertTrue(csv.contains("type,tourName,description"));
        String[] lines = csv.trim().split("\n");
        assertEquals(1, lines.length);
    }

    @Test
    void exportToCsv_withTourAndLog_containsBothRows() {
        User user = new User();
        user.setId(1L);
        user.setUsername("marco");

        Tour tour = Tour.builder()
                .id(1L).name("Vienna Hike").description("Nice hike")
                .fromLocation("Vienna").toLocation("Baden")
                .transportType(TransportType.WALKING)
                .distanceKm(30.0).estimatedTime(360)
                .user(user).build();

        TourLog log = TourLog.builder()
                .id(1L).dateTime(LocalDateTime.of(2026, 6, 10, 9, 0))
                .comment("Sunny day").difficulty(Difficulty.MEDIUM)
                .totalDistance(28.5).totalTime(340).rating(5)
                .tour(tour).user(user).build();

        when(tourRepository.findTourByUserId(1L)).thenReturn(List.of(tour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log));

        String csv = tourDataService.exportToCsv(1L);

        assertTrue(csv.contains("TOUR"));
        assertTrue(csv.contains("Vienna Hike"));
        assertTrue(csv.contains("LOG"));
        assertTrue(csv.contains("Sunny day"));
        String[] lines = csv.trim().split("\n");
        assertEquals(3, lines.length);
    }

    @Test
    void exportToCsv_escapesCommasInFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("marco");

        Tour tour = Tour.builder()
                .id(1L).name("Tour with, comma").description("desc")
                .fromLocation("Vienna").toLocation("Graz")
                .transportType(TransportType.CAR)
                .distanceKm(200.0).estimatedTime(120)
                .user(user).build();

        when(tourRepository.findTourByUserId(1L)).thenReturn(List.of(tour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(Collections.emptyList());

        String csv = tourDataService.exportToCsv(1L);

        assertTrue(csv.contains("\"Tour with, comma\""));
    }
}
