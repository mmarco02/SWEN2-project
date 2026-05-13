package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
class TourController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAll() {
        List<TourDTO> allTours = tourService.getAll()
                .stream()
                .map(TourDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(allTours);
    }

    @PostMapping("/create")
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tour) {
        TourDTO createdTour = TourDTO.fromEntity(tourService.createTour(tour.toEntity()));
        return ResponseEntity.ok(createdTour);
    }
}
