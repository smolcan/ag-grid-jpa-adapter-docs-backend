package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class HibernateSqlInspector implements StatementInspector {
    @Override
    public String inspect(String sql) {
        SqlQueryContext.addQuery(sql);
        return sql; // Return the SQL unchanged
    }
}
