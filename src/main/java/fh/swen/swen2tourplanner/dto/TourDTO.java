package fh.swen.swen2tourplanner.dto;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.enums.TransportType;

public record TourDTO(
        Long id,
        String name,
        String description,
        String fromLocation,
        String toLocation,
        TransportType transportType,
        double distanceKm,
        Integer estimatedTime,
        String route,
        Long userId
) {
    public static TourDTO fromEntity(Tour tour) {
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
                tour.getUser().getId()
        );
    }

    public Tour toEntity() {
        Tour tour = new Tour();
        tour.setId(id);
        tour.setName(name);
        tour.setDescription(description);
        tour.setFromLocation(fromLocation);
        tour.setToLocation(toLocation);
        tour.setTransportType(transportType);
        tour.setDistanceKm(distanceKm);
        tour.setEstimatedTime(estimatedTime);
        tour.setRoute(route);
        return tour;
    }
}
