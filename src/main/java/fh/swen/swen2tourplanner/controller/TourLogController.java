package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.service.TourLogService;
import fh.swen.swen2tourplanner.service.mappers.TourLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours/{tourId}/logs")
@RequiredArgsConstructor
class TourLogController {

    private final TourLogService tourLogService;
    private final TourLogMapper tourLogMapper;

    @GetMapping
    public ResponseEntity<List<TourLogDTO>> getByTourId(@PathVariable Long tourId) {
        List<TourLogDTO> logs = tourLogMapper.mapToDTOList(tourLogService.getByTourId(tourId));
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<TourLogDTO> create(@PathVariable Long tourId, @RequestBody TourLogDTO tourLogDTO) {
        try {
            TourLogDTO createdDTO = tourLogMapper.mapToDTO(
                    tourLogService.create(new TourLogDTO(
                            tourLogDTO.id(), tourLogDTO.dateTime(), tourLogDTO.comment(), tourLogDTO.difficulty(),
                            tourLogDTO.totalDistance(), tourLogDTO.totalTime(), tourLogDTO.rating(),
                            tourId, tourLogDTO.userId()
                    ))
            );
            return ResponseEntity.status(201).body(createdDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{logId}")
    public ResponseEntity<TourLogDTO> update(@PathVariable Long tourId, @PathVariable Long logId, @RequestBody TourLogDTO tourLogDTO) {
        try {
            TourLogDTO updatedDTO = tourLogMapper.mapToDTO(tourLogService.update(logId, tourLogDTO));
            return ResponseEntity.ok(updatedDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId, @PathVariable Long logId) {
        try {
            tourLogService.delete(logId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
