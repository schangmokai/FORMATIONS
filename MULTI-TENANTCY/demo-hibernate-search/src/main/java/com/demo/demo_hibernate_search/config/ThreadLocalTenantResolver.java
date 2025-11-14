package com.demo.demo_hibernate_search.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalTenantResolver implements CurrentTenantIdentifierResolver {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    public static void clear() {
        CURRENT_TENANT.remove();
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t = CURRENT_TENANT.get();
        // retourner null ou une valeur par défaut si souhaité
        return t;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
