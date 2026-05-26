package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.service.TourService;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
class TourController {

    private final TourService tourService;
    private final TourMapper tourMapper;

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAll() {
        List<TourDTO> allTours = tourMapper.mapToDTOList(tourService.getAll());
        return ResponseEntity.ok(allTours);
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourDTO> getById(@PathVariable Long tourId) {
        try {
            return ResponseEntity.ok(tourMapper.mapToDTO(tourService.getById(tourId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tour) {
        try {
            TourDTO createdTour = tourMapper.mapToDTO(tourService.createTour(tour));
            return ResponseEntity.status(201).body(createdTour);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<TourDTO> update(@PathVariable Long tourId, @RequestBody TourDTO tourDTO) {
        try {
            TourDTO updatedDTO = tourMapper.mapToDTO(tourService.updateTour(tourId, tourDTO));
            return ResponseEntity.ok(updatedDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId) {
        try {
            tourService.deleteTour(tourId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getFromTo")
    public ResponseEntity<List<TourDTO>> getToursFromTo(@RequestParam String from, @RequestParam String to){
        List<TourDTO> tourDTOS = tourMapper.mapToDTOList(tourService.getToursFromTo(from, to));
        return ResponseEntity.ok(tourDTOS);
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<List<TourDTO>> getToursFromUser(@PathVariable Long userId) {
        List<TourDTO> tourDTOS = tourMapper.mapToDTOList(tourService.getToursByUserId(userId));
        return ResponseEntity.ok(tourDTOS);
    }
}
