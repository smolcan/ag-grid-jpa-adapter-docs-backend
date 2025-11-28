package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@Component
public class TradeDataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    // Konfigurácia počtu záznamov pre referenčné tabuľky
    private static final int SUBMITTER_COUNT = 1000;
    private static final int DEAL_COUNT = 5000;
    private static final int TRADE_COUNT = 100_000;

    public TradeDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 1. Clean up & Create tables
            dropTables();
            createReferenceTables();
            createTradeTable();

            // Check if trade data exists (only checking trade is enough usually)
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trade", Integer.class);
            if (count != null && count > 0) {
                System.out.println("Data already exists, skipping initialization.");
                return;
            }

            System.out.println("Starting data initialization...");
            long startTime = System.currentTimeMillis();

            // 2. Insert Reference Data (Submitters, Deals)
            insertSubmitters();
            insertSubmitterDeals();

            // 3. Insert Trades using IDs from above
            insertTradeData();

            // 4. Add Constraints
            addForeignKeyConstraints();

            long endTime = System.currentTimeMillis();
            System.out.println("Successfully initialized all data in " + (endTime - startTime) + "ms!");

        } catch (Exception e) {
            System.err.println("Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dropTables() {
        // Drop trade first because it references others
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS trade");
            jdbcTemplate.execute("DROP TABLE IF EXISTS submitter_deal");
            jdbcTemplate.execute("DROP TABLE IF EXISTS submitter");
        } catch (Exception e) {
            // Ignore
        }
    }

    private void createReferenceTables() {
        System.out.println("Creating reference tables...");

        // Submitter table
        jdbcTemplate.execute("""
            CREATE TABLE submitter (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255)
            )
        """);

        // Submitter Deal table
        jdbcTemplate.execute("""
            CREATE TABLE submitter_deal (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255),
                created_at TIMESTAMP
            )
        """);
    }

    private void createTradeTable() {
        System.out.println("Creating trade table...");

        String createTableSql = """
            CREATE TABLE trade (
                trade_id IDENTITY PRIMARY KEY,
                parent_trade_id BIGINT,
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

        // Indexes for performance
        jdbcTemplate.execute("CREATE INDEX idx_trade_parent ON trade(parent_trade_id)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_submitter ON trade(submitter_id)");
        jdbcTemplate.execute("CREATE INDEX idx_trade_deal ON trade(submitter_deal_id)");

        System.out.println("Trade table created successfully.");
    }

    private void insertSubmitters() {
        System.out.println("Inserting " + SUBMITTER_COUNT + " submitters...");
        String sql = "INSERT INTO submitter (name) VALUES (?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 1; i <= SUBMITTER_COUNT; i++) {
            batchArgs.add(new Object[]{"Submitter " + i});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertSubmitterDeals() {
        System.out.println("Inserting " + DEAL_COUNT + " deals...");
        String sql = "INSERT INTO submitter_deal (name, created_at) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 1; i <= DEAL_COUNT; i++) {
            LocalDateTime randomDate = LocalDateTime.now().minusDays(random.nextInt(1000));
            batchArgs.add(new Object[]{
                    "Deal " + i,
                    Timestamp.valueOf(randomDate)
            });
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertTradeData() {
        System.out.println("Inserting " + TRADE_COUNT + " trades...");

        String sql = """
            INSERT INTO trade (
                parent_trade_id,
                product, portfolio, book, submitter_id, submitter_deal_id, 
                deal_type, bid_type, current_value, previous_value, 
                pl1, pl2, gain_dx, sx_px, x99_out, batch, birth_date, is_sold
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        int batchSize = 1000;
        int totalBatches = TRADE_COUNT / batchSize;

        for (int batch = 0; batch < totalBatches; batch++) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                int recordNumber = batch * batchSize + i + 1;

                // Hierarchy logic
                Long parentId = null;
                if (recordNumber > 100) {
                    if (random.nextDouble() > 0.05) {
                        if (random.nextBoolean()) {
                            int offset = random.nextInt(Math.min(recordNumber - 1, 500)) + 1;
                            parentId = (long) (recordNumber - offset);
                        } else {
                            parentId = (long) (random.nextInt(recordNumber - 1) + 1);
                        }
                    }
                }

                
                Long submitterId = random.nextDouble() < 0.1 ? null : (long) (random.nextInt(SUBMITTER_COUNT) + 1);
                Long dealId = random.nextDouble() < 0.1 ? null : (long) (random.nextInt(DEAL_COUNT) + 1);

                Object[] row = new Object[18];
                row[0] = parentId; // parent_trade_id

                row[1] = "Product " + (random.nextInt(10) + 1);
                row[2] = "Portfolio " + (random.nextInt(10) + 1);
                row[3] = "Book " + (random.nextInt(5) + 1);

                row[4] = submitterId;
                row[5] = dealId;

                row[6] = "Type " + (random.nextInt(3) + 1);
                row[7] = "Bid " + (random.nextInt(2) + 1);
                row[8] = random.nextDouble() < 0.2 ? null : random.nextDouble() * 10000;
                row[9] = random.nextDouble() < 0.2 ? null : random.nextDouble() * 10000;
                row[10] = random.nextDouble() * 100;
                row[11] = random.nextDouble() * 100;
                row[12] = random.nextDouble() * 50;
                row[13] = random.nextDouble() * 50;
                row[14] = random.nextDouble() * 50;
                row[15] = recordNumber % 100;
                row[16] = random.nextDouble() < 0.2 ? null : LocalDate.now().minusDays(recordNumber % 365);
                row[17] = random.nextDouble() < 0.2 ? null : (recordNumber % 2) == 0;

                batchArgs.add(row);
            }

            jdbcTemplate.batchUpdate(sql, batchArgs);

            if ((batch + 1) % 10 == 0) {
                System.out.println("Inserted " + ((batch + 1) * batchSize) + " records...");
            }
        }
    }

    private void addForeignKeyConstraints() {
        System.out.println("Adding Foreign Key constraints...");
        try {
            // Self-reference
            jdbcTemplate.execute("""
                ALTER TABLE trade 
                ADD CONSTRAINT fk_trade_parent 
                FOREIGN KEY (parent_trade_id) REFERENCES trade(trade_id)
            """);

            // FK to Submitter
            jdbcTemplate.execute("""
                ALTER TABLE trade 
                ADD CONSTRAINT fk_trade_submitter 
                FOREIGN KEY (submitter_id) REFERENCES submitter(id)
            """);

            // FK to Submitter Deal
            jdbcTemplate.execute("""
                ALTER TABLE trade 
                ADD CONSTRAINT fk_trade_deal 
                FOREIGN KEY (submitter_deal_id) REFERENCES submitter_deal(id)
            """);

            System.out.println("Foreign Key constraints added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add FK constraints: " + e.getMessage());
        }
    }
}