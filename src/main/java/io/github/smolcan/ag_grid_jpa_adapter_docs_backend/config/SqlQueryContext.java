package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.config;


import java.util.ArrayList;
import java.util.List;

public class SqlQueryContext {
    private static final ThreadLocal<List<String>> queries = ThreadLocal.withInitial(ArrayList::new);

    public static void addQuery(String sql) {
        queries.get().add(sql);
    }

    public static List<String> getQueries() {
        return queries.get();
    }

    public static void clear() {
        queries.remove();
    }
}