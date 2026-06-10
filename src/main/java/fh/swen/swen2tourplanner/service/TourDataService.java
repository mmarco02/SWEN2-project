package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.domain.User;
import fh.swen.swen2tourplanner.domain.enums.Difficulty;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import fh.swen.swen2tourplanner.exception.ResourceNotFoundException;
import fh.swen.swen2tourplanner.persistence.TourLogRepository;
import fh.swen.swen2tourplanner.persistence.TourRepository;
import fh.swen.swen2tourplanner.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourDataService {

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final UserRepository userRepository;
    private final TourService tourService;

    private static final char UTF8_BOM = '﻿';
    private static final String CSV_HEADER = "type,tourName,description,fromLocation,toLocation,transportType,distanceKm,estimatedTime,dateTime,comment,difficulty,totalDistance,totalTime,rating";

    public String exportToCsv(Long userId) {
        List<Tour> tours = tourRepository.findTourByUserId(userId);
        StringBuilder sb = new StringBuilder();
        sb.append(UTF8_BOM);
        sb.append(CSV_HEADER).append("\n");

        for (Tour tour : tours) {
            String[] tourRow = new String[14];
            tourRow[0] = "TOUR";
            tourRow[1] = escapeCsv(tour.getName());
            tourRow[2] = escapeCsv(tour.getDescription());
            tourRow[3] = escapeCsv(tour.getFromLocation());
            tourRow[4] = escapeCsv(tour.getToLocation());
            tourRow[5] = tour.getTransportType().name();
            tourRow[6] = String.valueOf(tour.getDistanceKm());
            tourRow[7] = tour.getEstimatedTime() != null ? String.valueOf(tour.getEstimatedTime()) : "";
            tourRow[8] = "";
            tourRow[9] = "";
            tourRow[10] = "";
            tourRow[11] = "";
            tourRow[12] = "";
            tourRow[13] = "";
            sb.append(String.join(",", tourRow)).append("\n");

            List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());
            for (TourLog tourLog : logs) {
                String[] logRow = new String[14];
                logRow[0] = "LOG";
                logRow[1] = escapeCsv(tour.getName());
                logRow[2] = "";
                logRow[3] = "";
                logRow[4] = "";
                logRow[5] = "";
                logRow[6] = "";
                logRow[7] = "";
                logRow[8] = tourLog.getDateTime().toString();
                logRow[9] = escapeCsv(tourLog.getComment() != null ? tourLog.getComment() : "");
                logRow[10] = tourLog.getDifficulty().name();
                logRow[11] = String.valueOf(tourLog.getTotalDistance());
                logRow[12] = String.valueOf(tourLog.getTotalTime());
                logRow[13] = String.valueOf(tourLog.getRating());
                sb.append(String.join(",", logRow)).append("\n");
            }
        }

        log.info("Exported {} tours for userId={}", tours.size(), userId);
        return sb.toString();
    }

    @Transactional
    public int importFromFile(Long userId, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        List<String[]> rows = new ArrayList<>();

        if (filename != null) {
            rows = parseCsv(file);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Map<String, Tour> createdTours = new LinkedHashMap<>();
        List<String[]> logRows = new ArrayList<>();
        int count = 0;

        for (String[] row : rows) {
            if (row.length < 2) continue;
            String type = row[0].trim().toUpperCase();

            if ("TOUR".equals(type)) {
                Tour tour = Tour.builder()
                        .name(row[1].trim())
                        .description(getField(row, 2))
                        .fromLocation(getField(row, 3))
                        .toLocation(getField(row, 4))
                        .transportType(TransportType.valueOf(getField(row, 5).toUpperCase()))
                        .distanceKm(parseDouble(getField(row, 6)))
                        .estimatedTime(parseInteger(getField(row, 7)))
                        .user(user)
                        .build();
                Tour saved = tourRepository.save(tour);
                createdTours.put(saved.getName(), saved);
                count++;
            } else if ("LOG".equals(type)) {
                logRows.add(row);
            }
        }

        for (String[] row : logRows) {
            String tourName = row[1].trim();
            Tour tour = createdTours.get(tourName);
            if (tour == null) {
                log.warn("Skipping log for unknown tour: {}", tourName);
                continue;
            }

            TourLog tourLog = TourLog.builder()
                    .dateTime(LocalDateTime.parse(getField(row, 8)))
                    .comment(getField(row, 9))
                    .difficulty(Difficulty.valueOf(getField(row, 10).toUpperCase()))
                    .totalDistance(parseDouble(getField(row, 11)))
                    .totalTime((int) parseDouble(getField(row, 12)))
                    .rating((int) parseDouble(getField(row, 13)))
                    .tour(tour)
                    .user(user)
                    .build();
            tourLogRepository.save(tourLog);
            count++;
        }

        for (Tour tour : createdTours.values()) {
            tourService.recalculateComputedAttributes(tour.getId());
        }

        log.info("Imported {} entries for userId={}", count, userId);
        return count;
    }

    private List<String[]> parseCsv(MultipartFile file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header != null && !header.isEmpty() && header.charAt(0) == UTF8_BOM) {
                header = header.substring(1);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String getField(String[] row, int index) {
        if (index >= row.length) return "";
        return row[index] != null ? row[index].trim() : "";
    }

    private double parseDouble(String value) {
        if (value == null || value.isBlank()) return 0;
        return Double.parseDouble(value);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;
        return (int) Double.parseDouble(value);
    }
}
