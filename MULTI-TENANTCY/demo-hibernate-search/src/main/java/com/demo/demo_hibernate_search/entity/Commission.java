package com.demo.demo_hibernate_search.entity;

import com.demo.demo_hibernate_search.config.TenantContext;
import com.demo.demo_hibernate_search.config.ThreadLocalTenantResolver;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TenantId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Table(name = "commission")
@Indexed
@Data
public class Commission {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_commission")
    @SequenceGenerator(name = "seq_commission", sequenceName = "seq_commission", allocationSize = 1)
    private Long id;

    @Column(name = "intitule", nullable = false)
    @FullTextField(analyzer = "myCustomAnalyzer")
    //@FullTextField(analyzer = "french")
    private String intitule;

    @Column(name = "intituleCourt", nullable = false)
    @FullTextField(analyzer = "myCustomAnalyzer")
    //@FullTextField(analyzer = "french")
    private String intituleCourt;

    @Column(name = "pcm")
    @FullTextField(analyzer = "myCustomAnalyzer")
    //@FullTextField(analyzer = "french")
    private String pcm;

    @TenantId  // Add this annotation
    private String tenantId;

    // Mais un champ normal pour Hibernate Search
    /*@TenantId
    @Transient  // ← Pas en base, calculé à la volée
    private String tenantId;

    public String getTenantId() {
        // Récupérer depuis le contexte ou autre logique
        return TenantContext.getCurrentTenantId();
    }*/
}
