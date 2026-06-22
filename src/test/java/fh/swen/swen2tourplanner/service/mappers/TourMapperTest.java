package fh.swen.swen2tourplanner.service.mappers;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.dto.TourDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TourMapperTest {

    private final TourMapper mapper = new TourMapper();

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("marco");
        return user;
    }

    @Test
    void mapToDTO_mapsAllFields() {
        User user = createTestUser();

        Tour tour = Tour.builder()
                .id(1L)
                .name("Test")
                .description("desc")
                .fromLocation("Vienna")
                .toLocation("Graz")
                .transportType(TransportType.CAR)
                .distanceKm(200.0)
                .estimatedTime(120)
                .route("route-data")
                .imagePath("img.jpg")
                .popularity("POPULAR")
                .childFriendliness("MODERATE")
                .user(user)
                .build();

        TourDTO dto = mapper.mapToDTO(tour);

        assertEquals(1L, dto.id());
        assertEquals("Test", dto.name());
        assertEquals("desc", dto.description());
        assertEquals("Vienna", dto.fromLocation());
        assertEquals("Graz", dto.toLocation());
        assertEquals(TransportType.CAR, dto.transportType());
        assertEquals(200.0, dto.distanceKm());
        assertEquals(120, dto.estimatedTime());
        assertEquals("route-data", dto.route());
        assertEquals("img.jpg", dto.imagePath());
        assertEquals("POPULAR", dto.popularity());
        assertEquals("MODERATE", dto.childFriendliness());
        assertEquals(1L, dto.userId());
        assertEquals("marco", dto.userUsername());
    }

    @Test
    void mapToEntity_mapsAllFields() {
        TourDTO dto = new TourDTO(1L, "Test", "desc", "Vienna", "Graz",
                TransportType.BICYCLE, 100.0, 180, "route", "img.png", 1L, "marco", null, null);

        Tour entity = mapper.mapToEntity(dto);

        assertEquals("Test", entity.getName());
        assertEquals("desc", entity.getDescription());
        assertEquals("Vienna", entity.getFromLocation());
        assertEquals("Graz", entity.getToLocation());
        assertEquals(TransportType.BICYCLE, entity.getTransportType());
        assertEquals(100.0, entity.getDistanceKm());
        assertEquals(180, entity.getEstimatedTime());
        assertEquals("route", entity.getRoute());
        assertEquals("img.png", entity.getImagePath());
    }

    @Test
    void mapToDTOList_mapsMultipleTours() {
        User user = createTestUser();

        Tour t1 = Tour.builder().id(1L).name("Tour 1").description("d").fromLocation("A").toLocation("B")
                .transportType(TransportType.CAR).user(user).build();
        Tour t2 = Tour.builder().id(2L).name("Tour 2").description("d").fromLocation("C").toLocation("D")
                .transportType(TransportType.WALKING).user(user).build();

        List<TourDTO> result = mapper.mapToDTOList(List.of(t1, t2));

        assertEquals(2, result.size());
        assertEquals("Tour 1", result.get(0).name());
        assertEquals("Tour 2", result.get(1).name());
    }

    @Test
    void updateEntity_updatesAllEditableFields() {
        User user = createTestUser();
        Tour tour = Tour.builder().id(1L).name("Old").description("old desc").fromLocation("A").toLocation("B")
                .transportType(TransportType.CAR).distanceKm(100.0).estimatedTime(60).user(user).build();

        TourDTO dto = new TourDTO(1L, "New", "new desc", "X", "Y",
                TransportType.BICYCLE, 200.0, 120, "new-route", null, 1L, "marco", null, null);

        mapper.updateEntity(tour, dto);

        assertEquals("New", tour.getName());
        assertEquals("new desc", tour.getDescription());
        assertEquals("X", tour.getFromLocation());
        assertEquals("Y", tour.getToLocation());
        assertEquals(TransportType.BICYCLE, tour.getTransportType());
        assertEquals(200.0, tour.getDistanceKm());
        assertEquals(120, tour.getEstimatedTime());
        assertEquals("new-route", tour.getRoute());
    }
}
