package com.demo.demo_hibernate_search.service;

import com.demo.demo_hibernate_search.dto.CommissionDTO;
import com.demo.demo_hibernate_search.entity.Commission;
import com.demo.demo_hibernate_search.mappers.Mappers;
import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceImpl implements IService{

    private final EntityManager entityManager;

    private Mappers mappers;

    public ServiceImpl(EntityManager entityManager, Mappers mappers) {
        this.entityManager = entityManager;
        this.mappers = mappers;
    }

    @Override
    public List<CommissionDTO> globalSearchByCommission(String searchTerm) {
        SearchSession searchSession = Search.session(entityManager);
        String searchTermWithoutSpace = searchTerm.replaceAll("\\W+", "").toLowerCase();
        SearchResult<Commission> result = searchSession.search(Commission.class)
                .where(f -> f.bool()
                        .should(f.wildcard()
                                .fields("intitule")
                                .matching("*" + searchTermWithoutSpace + "*")
                        )
                        .should(f.wildcard()
                                .fields("intituleCourt")
                                .matching("*" + searchTermWithoutSpace + "*")
                        )
                        .should(f.wildcard()
                                .fields("pcm")
                                .matching("*" + searchTermWithoutSpace + "*")
                        ))
                .fetch(1000);

        return result.hits().stream()
                .map(entity -> mappers.fromCommission(entity))
                .toList();
    }
}
