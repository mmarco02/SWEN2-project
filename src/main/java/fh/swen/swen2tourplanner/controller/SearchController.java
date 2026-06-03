package fh.swen.swen2tourplanner.controller;

import fh.swen.swen2tourplanner.dto.SearchResultDTO;
import fh.swen.swen2tourplanner.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResultDTO> getFullTextSearch(@RequestParam String query) {
        SearchResultDTO results = searchService.fullTextSearch(query);
        return ResponseEntity.ok(results);
    }
}
