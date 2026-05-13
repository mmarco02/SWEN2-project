package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.service.TourLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours/{tourId}/logs")
@RequiredArgsConstructor
class TourLogController {

    private final TourLogService tourLogService;

    @GetMapping
    public ResponseEntity<List<TourLogDTO>> getByTourId(@PathVariable Long tourId) {
        List<TourLogDTO> logs = tourLogService.getByTourId(tourId)
                .stream()
                .map(TourLogDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<TourLogDTO> create(@PathVariable Long tourId, @RequestBody TourLogDTO tourLogDTO) {
        TourLogDTO createdDTO = TourLogDTO.fromEntity(
                tourLogService.create(new TourLogDTO(
                        tourLogDTO.id(), tourLogDTO.dateTime(), tourLogDTO.comment(), tourLogDTO.difficulty(),
                        tourLogDTO.totalDistance(), tourLogDTO.totalTime(), tourLogDTO.rating(),
                        tourId, tourLogDTO.userId()
                ))
        );
        return ResponseEntity.ok(createdDTO);
    }

    @PutMapping("/{logId}")
    public ResponseEntity<TourLogDTO> update(@PathVariable Long tourId, @PathVariable Long logId, @RequestBody TourLogDTO tourLogDTO) {
        TourLogDTO updatedDTO = TourLogDTO.fromEntity(tourLogService.update(logId, tourLogDTO));
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId, @PathVariable Long logId) {
        tourLogService.delete(logId);
        return ResponseEntity.noContent().build();
    }
}
