package fh.swen.swen2tourplanner.service;

import fh.swen.swen2tourplanner.domain.SearchResult;
import fh.swen.swen2tourplanner.domain.Tour;
import fh.swen.swen2tourplanner.domain.TourLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger logger = LogManager.getLogger(SearchService.class);

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public SearchResult fullTextSearch(String query) {
        logger.info("Full-text search: query=\"{}\"", query);

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

        logger.debug("Search results: {} tours, {} tour logs", tours.size(), tourLogs.size());

        return new SearchResult(tours, tourLogs);
    }

    // Indexes all Entities on Backend Startup (because normally data added from data.sql on startup isnt)
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void rebuildIndex() throws InterruptedException {
        logger.info("Rebuilding Hibernate Search index");
        Search.session(entityManager).massIndexer().startAndWait();
        logger.info("Index rebuild complete");
    }
}
