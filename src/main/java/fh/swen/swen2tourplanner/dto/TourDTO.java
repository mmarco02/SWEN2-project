package fh.swen.swen2tourplanner.dto;

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
        String imagePath,
        Long userId,
        String userUsername,
        String popularity,
        String childFriendliness
) {
}
