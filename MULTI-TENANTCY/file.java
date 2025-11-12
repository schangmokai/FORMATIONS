package com.demo.demo_hibernate_search.config;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class SchemaMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return new SimpleConnectionProvider(dataSource);
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return new SchemaConnectionProvider(dataSource, tenantIdentifier);
    }

    static class SimpleConnectionProvider implements ConnectionProvider  {
        protected final DataSource dataSource;

        SimpleConnectionProvider(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
            conn.close();
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }
    }

    static class SchemaConnectionProvider extends SimpleConnectionProvider {
        private final String schema;

        SchemaConnectionProvider(DataSource dataSource, String schema) {
            super(dataSource);
            this.schema = schema;
        }

        @Override
        public Connection getConnection() throws SQLException {
            Connection connection = super.getConnection();
            // PostgreSQL : on change le search_path
            connection.createStatement().execute("SET search_path TO " + schema);
            return connection;
        }
    }
}