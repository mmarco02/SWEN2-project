package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.service.ImageStorageService;
import fh.swen.swen2tourplanner.service.TourService;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
class TourController {

    private final TourService tourService;
    private final TourMapper tourMapper;
    private final ImageStorageService imageStorageService;

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

    @PostMapping("/{tourId}/image")
    public ResponseEntity<TourDTO> uploadImage(@PathVariable Long tourId, @RequestParam("file") MultipartFile file) {
        try {
            TourDTO updated = tourService.uploadImage(tourId, file);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{tourId}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long tourId) {
        try {
            var tour = tourService.getById(tourId);
            if (tour.getImagePath() == null) {
                return ResponseEntity.notFound().build();
            }
            Path filePath = imageStorageService.load(tour.getImagePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + tour.getImagePath() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
