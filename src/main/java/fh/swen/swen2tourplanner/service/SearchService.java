package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.SearchResult;
import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public SearchResult fullTextSearch(String query) {
        log.info("Full-text search: query=\"{}\"", query);

        SearchSession searchSession = Search.session(entityManager);

        List<Tour> tours = searchSession.search(Tour.class)
                .where(f -> f.simpleQueryString()
                        .fields("name", "description", "fromLocation", "toLocation", "transportType")
                        .matching(query))
                .fetchAllHits();

        List<TourLog> tourLogs = searchSession.search(TourLog.class)
                .where(f -> f.simpleQueryString()
                        .fields("comment", "difficulty")
                        .matching(query))
                .fetchAllHits();

        log.debug("Search results: {} tours, {} tour logs", tours.size(), tourLogs.size());

        return new SearchResult(tours, tourLogs);
    }

    // Indexes all Entities on Backend Startup (because normally data added from data.sql on startup isnt)
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void rebuildIndex() throws InterruptedException {
        log.info("Rebuilding Hibernate Search index");
        Search.session(entityManager).massIndexer().startAndWait();
        log.info("Index rebuild complete");
    }
}
