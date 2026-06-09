package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import fh.swen.swen2tourplanner.dto.SearchResultDTO;
import fh.swen.swen2tourplanner.service.mappers.TourLogMapper;
import fh.swen.swen2tourplanner.service.mappers.TourMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final TourMapper tourMapper;
    private final TourLogMapper tourLogMapper;

    @Transactional(readOnly = true)
    public SearchResultDTO fullTextSearch(String query) {
        log.info("Full-text search: query=\"{}\"", query);

        String fuzzyQuery = Arrays.stream(query.trim().split("\\s+"))
                .map(term -> term + "~")
                .collect(Collectors.joining(" "));

        SearchSession searchSession = Search.session(entityManager);

        List<Tour> tours = searchSession.search(Tour.class)
                .where(f -> f.simpleQueryString()
                        .fields("name", "description", "fromLocation", "toLocation", "transportType", "popularity", "childFriendliness")
                        .matching(fuzzyQuery))
                .fetchAllHits();

        List<TourLog> tourLogs = searchSession.search(TourLog.class)
                .where(f -> f.simpleQueryString()
                        .fields("comment", "difficulty")
                        .matching(fuzzyQuery))
                .fetchAllHits();

        log.debug("Search results: {} tours, {} tour logs", tours.size(), tourLogs.size());

        return new SearchResultDTO(tourMapper.mapToDTOList(tours), tourLogMapper.mapToDTOList(tourLogs));
    }

    // Indexes all Entities on Backend Startup (because normally data added from data.sql on startup isnt)
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Order(2)
    public void rebuildIndex() throws InterruptedException {
        log.info("Rebuilding Hibernate Search index");
        Search.session(entityManager).massIndexer().startAndWait();
        log.info("Index rebuild complete");
    }
}
