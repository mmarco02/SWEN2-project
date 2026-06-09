package fh.swen.swen2tourplanner.service.mappers;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.dto.TourDTO;
import org.springframework.stereotype.Component;

@Component
public class TourMapper extends AbstractMapper<Tour, TourDTO> {

    @Override
    public TourDTO mapToDTO(Tour tour) {
        return new TourDTO(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getFromLocation(),
                tour.getToLocation(),
                tour.getTransportType(),
                tour.getDistanceKm(),
                tour.getEstimatedTime(),
                tour.getRoute(),
                tour.getImagePath(),
                tour.getUser().getId(),
                tour.getUser().getUsername(),
                tour.getPopularity(),
                tour.getChildFriendliness()
        );
    }

    @Override
    public Tour mapToEntity(TourDTO dto) {
        return Tour.builder()
                .id(dto.id())
                .name(dto.name())
                .description(dto.description())
                .fromLocation(dto.fromLocation())
                .toLocation(dto.toLocation())
                .transportType(dto.transportType())
                .distanceKm(dto.distanceKm())
                .estimatedTime(dto.estimatedTime())
                .route(dto.route())
                .imagePath(dto.imagePath())
                .build();
    }

    @Override
    public void updateEntity(Tour tour, TourDTO dto) {
        tour.setName(dto.name());
        tour.setDescription(dto.description());
        tour.setFromLocation(dto.fromLocation());
        tour.setToLocation(dto.toLocation());
        tour.setTransportType(dto.transportType());
        tour.setDistanceKm(dto.distanceKm());
        tour.setEstimatedTime(dto.estimatedTime());
        tour.setRoute(dto.route());
    }
}
