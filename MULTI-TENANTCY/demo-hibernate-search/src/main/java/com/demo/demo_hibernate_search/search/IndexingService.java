package com.demo.demo_hibernate_search.search;

import com.demo.demo_hibernate_search.config.TenantContext;
import com.demo.demo_hibernate_search.entity.Commission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.TenantId;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndexingService {
    private final EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory;


    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);


    public IndexingService(EntityManager entityManager, EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    /*@Transactional
    public void initiateIndexing() throws InterruptedException {
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer indexer = searchSession.massIndexer(Commission.class);
        indexer.startAndWait();
    }*/

    public void initiateIndexingForTenant(String tenantId) throws InterruptedException {
        log.info("Démarrage de l'indexation pour le tenant: {}", tenantId);
        // Obtenir la SessionFactory depuis EntityManagerFactory
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        TenantContext.setCurrentTenant(tenantId);
        Session tenantSession = null;
        try {
            // Créer une session avec le tenant ID
            tenantSession = sessionFactory
                    .withOptions()
                    .tenantIdentifier((Object) tenantId)
                    .openSession();

            SearchSession searchSession = Search.session(tenantSession);

            searchSession.massIndexer(Commission.class)
                    .threadsToLoadObjects(4)
                    .batchSizeToLoadObjects(25)
                    .startAndWait();

            log.info("Indexation terminée avec succès pour le tenant: {}", tenantId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Indexation interrompue pour le tenant: {}", tenantId, e);
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'indexation pour le tenant: {}", tenantId, e);
            throw new RuntimeException("Erreur d'indexation pour tenant " + tenantId, e);
        } finally {
            if (tenantSession != null && tenantSession.isOpen()) {
                tenantSession.close();
                log.debug("Session fermée pour le tenant: {}", tenantId);
            }
        }
    }

    public void initiateIndexingForAllTenants(List<String> tenantIds) {
        log.info("Indexation de {} tenants", tenantIds.size());

        for (String tenantId : tenantIds) {
            try {
                initiateIndexingForTenant(tenantId);
            } catch (Exception e) {
                log.error("Échec de l'indexation pour le tenant: {}", tenantId, e);
                // Continuer avec les autres tenants
            }
        }

        log.info("Indexation de tous les tenants terminée");
    }

}
