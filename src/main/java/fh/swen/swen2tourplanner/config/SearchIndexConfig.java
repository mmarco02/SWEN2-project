package fh.swen.swen2tourplanner.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchIndexConfig {

    private static final Logger logger = LogManager.getLogger(SearchIndexConfig.class);

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void rebuildIndex() throws InterruptedException {
        logger.info("Rebuilding Hibernate Search index on startup");
        Search.session(entityManager).massIndexer().startAndWait();
        logger.info("Hibernate Search index rebuild complete");
    }
}
