package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.dto.ImageResource;
import fh.swen.swen2tourplanner.dto.TourDTO;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourImageService {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final ImageStorageService imageStorageService;

    @Transactional
    public TourDTO uploadImage(Long tourId, MultipartFile file) throws IOException {
        Tour tour = findTourOrThrow(tourId);

        if (tour.getImagePath() != null) {
            imageStorageService.delete(tour.getImagePath());
        }

        String filename = imageStorageService.store(file);
        tour.setImagePath(filename);
        Tour saved = tourRepository.save(tour);
        log.info("Image uploaded for tour: id={}", tourId);
        return tourMapper.mapToDTO(saved);
    }

    public ImageResource getImage(Long tourId) throws IOException {
        Tour tour = findTourOrThrow(tourId);

        if (tour.getImagePath() == null) {
            return null;
        }

        Path filePath = imageStorageService.load(tour.getImagePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return null;
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return new ImageResource(resource, contentType, tour.getImagePath());
    }

    public void deleteImageForTour(Tour tour) {
        if (tour.getImagePath() != null) {
            try {
                imageStorageService.delete(tour.getImagePath());
            } catch (IOException e) {
                log.warn("Failed to delete image for tour: id={}", tour.getId());
            }
        }
    }

    private Tour findTourOrThrow(Long tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", tourId));
    }
}
