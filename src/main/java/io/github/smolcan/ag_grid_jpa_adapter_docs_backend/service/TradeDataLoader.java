package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@Component
public class TradeDataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public TradeDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            
            createTradeTable();

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trade", Integer.class);
            if (count != null && count > 0) {
                System.out.println("Trade data already exists (" + count + " records), skipping initialization.");
                return;
            }

            System.out.println("Starting to insert 100,000 trade records with Hierarchy...");
            long startTime = System.currentTimeMillis();

            
            insertTradeData();

            addForeignKeyConstraint();

            long endTime = System.currentTimeMillis();
            System.out.println("Successfully inserted and linked 100,000 trade records in " + (endTime - startTime) + "ms!");

        } catch (Exception e) {
            System.err.println("Error during trade data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTradeTable() {
        System.out.println("Creating trade table...");

        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS trade");
        } catch (Exception e) {
            // Ignore
        }

        String createTableSql = """
            CREATE TABLE trade (
                trade_id IDENTITY PRIMARY KEY,
                parent_trade_id BIGINT,
                product VARCHAR(255),
                portfolio VARCHAR(255),
                book VARCHAR(255),
                submitter_id INTEGER,
                submitter_deal_id INTEGER,
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
        jdbcTemplate.execute("CREATE INDEX idx_trade_parent ON trade(parent_trade_id)");

        System.out.println("Trade table created successfully.");
    }

    private void insertTradeData() {
        String sql = """
            INSERT INTO trade (
                parent_trade_id,
                product, portfolio, book, submitter_id, submitter_deal_id, 
                deal_type, bid_type, current_value, previous_value, 
                pl1, pl2, gain_dx, sx_px, x99_out, batch, birth_date, is_sold
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        int batchSize = 1000;
        int totalBatches = 100; // 100,000 records

        for (int batch = 0; batch < totalBatches; batch++) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                int recordNumber = batch * batchSize + i + 1;

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

                Object[] row = new Object[18];
                row[0] = parentId; // parent_trade_id

                row[1] = "Product " + (random.nextInt(10) + 1);
                row[2] = "Portfolio " + (random.nextInt(10) + 1);
                row[3] = "Book " + (random.nextInt(5) + 1);
                row[4] = recordNumber * 10;
                row[5] = random.nextDouble() < 0.2 ? null : recordNumber * 3;
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

    private void addForeignKeyConstraint() {
        System.out.println("Adding Foreign Key constraint...");
        try {
            String sql = """
                ALTER TABLE trade 
                ADD CONSTRAINT fk_trade_parent 
                FOREIGN KEY (parent_trade_id) REFERENCES trade(trade_id)
            """;
            jdbcTemplate.execute(sql);
            System.out.println("Foreign Key constraint added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add FK constraint: " + e.getMessage());
        }
    }
}