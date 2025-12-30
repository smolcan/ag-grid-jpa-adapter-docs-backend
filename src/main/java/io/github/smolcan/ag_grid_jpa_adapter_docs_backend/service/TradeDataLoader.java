package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class TradeDataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    private static final int SUBMITTER_COUNT = 1000;
    private static final int DEAL_COUNT = 5000;
    private static final int TRADE_COUNT = 100_000;
    private static final String PATH_SEPARATOR = "/";

    public TradeDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            dropTables();
            createReferenceTables();
            createTradeTable();

            System.out.println("Starting data initialization with Data Path...");
            long startTime = System.currentTimeMillis();

            insertSubmitters();
            insertSubmitterDeals();
            insertTradeData(); // Tu sa generuje data_path

            addForeignKeyConstraints();

            long endTime = System.currentTimeMillis();
            System.out.println("Successfully initialized all data in " + (endTime - startTime) + "ms!");

        } catch (Exception e) {
            System.err.println("Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dropTables() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS trade");
        jdbcTemplate.execute("DROP TABLE IF EXISTS submitter_deal");
        jdbcTemplate.execute("DROP TABLE IF EXISTS submitter");
    }

    private void createReferenceTables() {
        jdbcTemplate.execute("CREATE TABLE submitter (id IDENTITY PRIMARY KEY, name VARCHAR(255))");
        jdbcTemplate.execute("CREATE TABLE submitter_deal (id IDENTITY PRIMARY KEY, name VARCHAR(255), created_at TIMESTAMP)");
    }

    private void createTradeTable() {
        String createTableSql = """
            CREATE TABLE trade (
                trade_id BIGINT PRIMARY KEY,
                parent_trade_id BIGINT,
                data_path VARCHAR(2000),
                product VARCHAR(255),
                portfolio VARCHAR(255),
                book VARCHAR(255),
                submitter_id BIGINT,
                submitter_deal_id BIGINT,
                deal_type VARCHAR(255),
                bid_type VARCHAR(255),
                current_value DECIMAL,
                previous_value DECIMAL,
                pl1 DECIMAL,
                pl2 DECIMAL,
                gain_dx DECIMAL,
                sx_px DECIMAL,
                x99_out DECIMAL,
                batch INTEGER,
                birth_date DATE,
                is_sold BOOLEAN
            )
            """;
        jdbcTemplate.execute(createTableSql);
        jdbcTemplate.execute("CREATE INDEX idx_trade_path ON trade(data_path)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_parent ON trade(parent_trade_id)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_product ON trade(product)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_current_value ON trade(current_value)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_previous_value ON trade(previous_value)");
    }

    private void insertTradeData() {
        System.out.println("Inserting " + TRADE_COUNT + " trades with hierarchy...");

        String sql = """
            INSERT INTO trade (
                trade_id, parent_trade_id, data_path,
                product, portfolio, book, submitter_id, submitter_deal_id, 
                deal_type, bid_type, current_value, previous_value, 
                pl1, pl2, gain_dx, sx_px, x99_out, batch, birth_date, is_sold
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        
        Map<Long, String> idToPathMap = new HashMap<>(TRADE_COUNT);

        int batchSize = 1000;
        int totalBatches = TRADE_COUNT / batchSize;

        for (int batch = 0; batch < totalBatches; batch++) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                long currentId = (long) (batch * batchSize + i + 1);

                // Logika hierarchie (rodič musí mať nižšie ID ako dieťa, aby sme ho mali v mape)
                Long parentId = null;
                String currentPath;

                if (currentId > 50 && random.nextDouble() > 0.3) {
                    // Vyberieme náhodného existujúceho rodiča z už spracovaných ID
                    parentId = (long) (random.nextInt((int) currentId - 1) + 1);
                    String parentPath = idToPathMap.get(parentId);
                    currentPath = parentPath + PATH_SEPARATOR + currentId;
                } else {
                    // Root element
                    currentPath = String.valueOf(currentId);
                }

                idToPathMap.put(currentId, currentPath);

                Long submitterId = random.nextDouble() < 0.1 ? null : (long) (random.nextInt(SUBMITTER_COUNT) + 1);
                Long dealId = random.nextDouble() < 0.1 ? null : (long) (random.nextInt(DEAL_COUNT) + 1);

                Object[] row = new Object[]{
                        currentId, parentId, currentPath,
                        "Product " + (random.nextInt(10) + 1),
                        "Portfolio " + (random.nextInt(10) + 1),
                        "Book " + (random.nextInt(5) + 1),
                        submitterId, dealId,
                        "Type " + (random.nextInt(3) + 1),
                        "Bid " + (random.nextInt(2) + 1),
                        random.nextDouble() * 10000, random.nextDouble() * 10000,
                        random.nextDouble() * 100, random.nextDouble() * 100,
                        random.nextDouble() * 50, random.nextDouble() * 50, random.nextDouble() * 50,
                        (int) (currentId % 100),
                        LocalDate.now().minusDays(currentId % 365),
                        (currentId % 2) == 0
                };
                batchArgs.add(row);
            }
            jdbcTemplate.batchUpdate(sql, batchArgs);

            if ((batch + 1) % 20 == 0) {
                System.out.println("Inserted " + ((batch + 1) * batchSize) + " records...");
            }
        }
        idToPathMap.clear(); // Uvoľnenie pamäte
    }

    // Ostatné metódy (insertSubmitters, addForeignKeyConstraints...) ostávajú podobné
    private void insertSubmitters() {
        String sql = "INSERT INTO submitter (name) VALUES (?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 1; i <= SUBMITTER_COUNT; i++) batchArgs.add(new Object[]{"Submitter " + i});
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertSubmitterDeals() {
        String sql = "INSERT INTO submitter_deal (name, created_at) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 1; i <= DEAL_COUNT; i++) {
            batchArgs.add(new Object[]{"Deal " + i, Timestamp.valueOf(LocalDateTime.now().minusDays(random.nextInt(1000)))});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void addForeignKeyConstraints() {
        try {
            jdbcTemplate.execute("ALTER TABLE trade ADD CONSTRAINT fk_trade_parent FOREIGN KEY (parent_trade_id) REFERENCES trade(trade_id)");
            jdbcTemplate.execute("ALTER TABLE trade ADD CONSTRAINT fk_trade_submitter FOREIGN KEY (submitter_id) REFERENCES submitter(id)");
            jdbcTemplate.execute("ALTER TABLE trade ADD CONSTRAINT fk_trade_deal FOREIGN KEY (submitter_deal_id) REFERENCES submitter_deal(id)");
        } catch (Exception e) {
            System.err.println("FK constraints notice: " + e.getMessage());
        }
    }
}