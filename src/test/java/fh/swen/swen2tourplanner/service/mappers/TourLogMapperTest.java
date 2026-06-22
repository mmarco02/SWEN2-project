package fh.swen.swen2tourplanner.service.mappers;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.Difficulty;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.dto.TourLogDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TourLogMapperTest {

    private final TourLogMapper mapper = new TourLogMapper();

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("marco");
        return user;
    }

    private Tour createTestTour(User user) {
        return Tour.builder().id(1L).name("Tour").description("d")
                .fromLocation("A").toLocation("B").transportType(TransportType.CAR).user(user).build();
    }

    @Test
    void mapToDTO_mapsAllFields() {
        User user = createTestUser();
        Tour tour = createTestTour(user);

        TourLog log = TourLog.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 6, 15, 10, 30))
                .comment("Great hike")
                .difficulty(Difficulty.MEDIUM)
                .totalDistance(12.5)
                .totalTime(90)
                .rating(4)
                .tour(tour)
                .user(user)
                .build();

        TourLogDTO dto = mapper.mapToDTO(log);

        assertEquals(1L, dto.id());
        assertEquals(LocalDateTime.of(2026, 6, 15, 10, 30), dto.dateTime());
        assertEquals("Great hike", dto.comment());
        assertEquals(Difficulty.MEDIUM, dto.difficulty());
        assertEquals(12.5, dto.totalDistance());
        assertEquals(90, dto.totalTime());
        assertEquals(4, dto.rating());
        assertEquals(1L, dto.tourId());
        assertEquals(1L, dto.userId());
    }

    @Test
    void mapToEntity_mapsAllFields() {
        TourLogDTO dto = new TourLogDTO(1L, LocalDateTime.of(2026, 6, 15, 10, 30),
                "Great hike", Difficulty.HARD, 20.0, 120, 3, 1L, 1L);

        TourLog entity = mapper.mapToEntity(dto);

        assertEquals("Great hike", entity.getComment());
        assertEquals(Difficulty.HARD, entity.getDifficulty());
        assertEquals(20.0, entity.getTotalDistance());
        assertEquals(120, entity.getTotalTime());
        assertEquals(3, entity.getRating());
    }

    @Test
    void updateEntity_updatesAllFields() {
        User user = createTestUser();
        Tour tour = createTestTour(user);

        TourLog log = TourLog.builder()
                .id(1L).dateTime(LocalDateTime.of(2026, 1, 1, 0, 0))
                .comment("Old").difficulty(Difficulty.EASY)
                .totalDistance(5.0).totalTime(30).rating(2)
                .tour(tour).user(user).build();

        TourLogDTO dto = new TourLogDTO(1L, LocalDateTime.of(2026, 6, 15, 10, 30),
                "Updated", Difficulty.EXPERT, 50.0, 300, 5, 1L, 1L);

        mapper.updateEntity(log, dto);

        assertEquals(LocalDateTime.of(2026, 6, 15, 10, 30), log.getDateTime());
        assertEquals("Updated", log.getComment());
        assertEquals(Difficulty.EXPERT, log.getDifficulty());
        assertEquals(50.0, log.getTotalDistance());
        assertEquals(300, log.getTotalTime());
        assertEquals(5, log.getRating());
    }

    @Test
    void mapToDTOList_mapsMultipleLogs() {
        User user = createTestUser();
        Tour tour = createTestTour(user);

        TourLog log1 = TourLog.builder().id(1L).dateTime(LocalDateTime.now())
                .comment("Log 1").difficulty(Difficulty.EASY).totalDistance(5.0)
                .totalTime(30).rating(3).tour(tour).user(user).build();
        TourLog log2 = TourLog.builder().id(2L).dateTime(LocalDateTime.now())
                .comment("Log 2").difficulty(Difficulty.HARD).totalDistance(25.0)
                .totalTime(180).rating(5).tour(tour).user(user).build();

        List<TourLogDTO> result = mapper.mapToDTOList(List.of(log1, log2));

        assertEquals(2, result.size());
        assertEquals("Log 1", result.get(0).comment());
        assertEquals("Log 2", result.get(1).comment());
    }
}
