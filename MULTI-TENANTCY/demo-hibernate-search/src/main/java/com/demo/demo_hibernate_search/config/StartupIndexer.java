package com.demo.demo_hibernate_search.config;

import com.demo.demo_hibernate_search.search.IndexingService;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartupIndexer {
    private final IndexingService indexingService;
    private static final Logger log = LoggerFactory.getLogger(StartupIndexer.class);

    public StartupIndexer(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostConstruct
    public void init() throws Exception {
        //indexingService.initiateIndexing();
        log.info("Démarrage de l'indexation en arrière-plan pour tous les tenants...");

        CompletableFuture.runAsync(() -> {
            List<String> tenantIds = Arrays.asList("tenant1", "tenant2", "tenant3");
            indexingService.initiateIndexingForAllTenants(tenantIds);
            log.info("Indexation en arrière-plan terminée");
        });
    }
}
