package fh.swen.swen2tourplanner.service.mappers;

import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.dto.TourLogDTO;
import org.springframework.stereotype.Component;

@Component
public class TourLogMapper extends AbstractMapper<TourLog, TourLogDTO> {

    @Override
    public TourLogDTO mapToDTO(TourLog tourLog) {
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

    @Override
    public TourLog mapToEntity(TourLogDTO dto) {
        return TourLog.builder()
                .id(dto.id())
                .dateTime(dto.dateTime())
                .comment(dto.comment())
                .difficulty(dto.difficulty())
                .totalDistance(dto.totalDistance())
                .totalTime(dto.totalTime())
                .rating(dto.rating())
                .build();
    }
}
