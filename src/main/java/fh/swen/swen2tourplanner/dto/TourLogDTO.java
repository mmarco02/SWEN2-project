package fh.swen.swen2tourplanner.dto;

import fh.swen.swen2tourplanner.domain.TourLog;
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
    public static TourLogDTO fromEntity(TourLog tourLog) {
        return new TourLogDTO(
                tourLog.getId(),
                tourLog.getDateTime(),
                tourLog.getComment(),
                tourLog.getDifficulty(),
                tourLog.getTotalDistance(),
                tourLog.getTotalTime(),
                tourLog.getRating(),
                tourLog.getTour().getId(),
                tourLog.getUser().getId()
        );
    }

    public TourLog toEntity() {
        TourLog tourLog = new TourLog();
        tourLog.setId(id);
        tourLog.setDateTime(dateTime);
        tourLog.setComment(comment);
        tourLog.setDifficulty(difficulty);
        tourLog.setTotalDistance(totalDistance);
        tourLog.setTotalTime(totalTime);
        tourLog.setRating(rating);
        return tourLog;
    }
}
