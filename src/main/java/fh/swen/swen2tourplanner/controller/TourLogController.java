package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourLogDTO;
import fh.swen.swen2tourplanner.service.TourLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(tourLogService.getByTourId(tourId));
    }

    @PostMapping
    public ResponseEntity<TourLogDTO> create(@PathVariable Long tourId, @RequestBody TourLogDTO tourLogDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourLogService.create(tourId, tourLogDTO));
    }

    @PutMapping("/{logId}")
    public ResponseEntity<TourLogDTO> update(@PathVariable Long tourId, @PathVariable Long logId, @RequestBody TourLogDTO tourLogDTO) {
        return ResponseEntity.ok(tourLogService.update(logId, tourLogDTO));
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId, @PathVariable Long logId) {
        tourLogService.delete(logId);
        return ResponseEntity.noContent().build();
    }
}
