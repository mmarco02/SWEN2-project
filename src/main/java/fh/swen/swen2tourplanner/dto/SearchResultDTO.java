package fh.swen.swen2tourplanner.dto;

import java.util.List;

public record SearchResultDTO(
        List<TourDTO> foundTours,
        List<TourLogDTO> foundLogs
) {
}
