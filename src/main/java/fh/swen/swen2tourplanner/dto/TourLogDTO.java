package fh.swen.swen2tourplanner.dto;

import fh.swen.swen2tourplanner.domain.enums.Difficulty;

import java.time.LocalDateTime;

public record TourLogDTO(
        Long id,
        LocalDateTime dateTime,
        String comment,
        Difficulty difficulty,
        double totalDistance,
        int totalTime,
        int rating,
        Long tourId,
        Long userId
) {
}
