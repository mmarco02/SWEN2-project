package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.ImageResource;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.service.TourImageService;
import fh.swen.swen2tourplanner.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
class TourController {

    private final TourService tourService;
    private final TourImageService tourImageService;

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAll() {
        return ResponseEntity.ok(tourService.getAll());
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourDTO> getById(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourService.getById(tourId));
    }

    @PostMapping("/create")
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tour) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tourService.createTour(tour));
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<TourDTO> update(@PathVariable Long tourId, @RequestBody TourDTO tourDTO) {
        return ResponseEntity.ok(tourService.updateTour(tourId, tourDTO));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId) {
        tourService.deleteTour(tourId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getFromTo")
    public ResponseEntity<List<TourDTO>> getToursFromTo(@RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(tourService.getToursFromTo(from, to));
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<List<TourDTO>> getToursFromUser(@PathVariable Long userId) {
        return ResponseEntity.ok(tourService.getToursByUserId(userId));
    }

    @PostMapping("/{tourId}/image")
    public ResponseEntity<TourDTO> uploadImage(@PathVariable Long tourId, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(tourImageService.uploadImage(tourId, file));
    }

    @GetMapping("/{tourId}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long tourId) throws IOException {
        ImageResource image = tourImageService.getImage(tourId);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.filename() + "\"")
                .contentType(MediaType.parseMediaType(image.contentType()))
                .body(image.resource());
    }
}
