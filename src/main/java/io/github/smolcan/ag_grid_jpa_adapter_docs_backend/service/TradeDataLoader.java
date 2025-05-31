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
            // Create table if it doesn't exist
            createTradeTable();

            // Check if data already exists
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trade", Integer.class);
            if (count != null && count > 0) {
                System.out.println("Trade data already exists (" + count + " records), skipping initialization.");
                return;
            }

            System.out.println("Starting to insert 100,000 trade records...");
            long startTime = System.currentTimeMillis();

            // Insert data in batches for better performance
            insertTradeData();

            long endTime = System.currentTimeMillis();
            System.out.println("Successfully inserted 100,000 trade records in " + (endTime - startTime) + "ms!");

        } catch (Exception e) {
            System.err.println("Error during trade data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTradeTable() {
        System.out.println("Creating trade table...");

        // Drop table if exists (optional - for clean start during development)
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS trade");
        } catch (Exception e) {
            // Ignore if table doesn't exist
        }

        // Create the trade table
        String createTableSql = """
            CREATE TABLE trade (
                trade_id IDENTITY PRIMARY KEY,
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
        System.out.println("Trade table created successfully.");
    }

    private void insertTradeData() {
        String sql = """
            INSERT INTO trade (
                product, portfolio, book, submitter_id, submitter_deal_id, 
                deal_type, bid_type, current_value, previous_value, 
                pl1, pl2, gain_dx, sx_px, x99_out, batch, birth_date, is_sold
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        int batchSize = 1000;
        int totalBatches = 100; // 100 batches * 1000 records = 100,000 records

        for (int batch = 0; batch < totalBatches; batch++) {
            List<Object[]> batchArgs = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                int recordNumber = batch * batchSize + i + 1;

                // Generate data with same logic as PostgreSQL version
                Object[] row = new Object[17];
                row[0] = "Product " + (random.nextInt(10) + 1);  // Random product 1-10
                row[1] = "Portfolio " + (random.nextInt(10) + 1);  // Random portfolio 1-10
                row[2] = "Book " + (random.nextInt(5) + 1);  // Random book 1-5
                row[3] = recordNumber * 10;  // submitter_id
                row[4] = random.nextDouble() < 0.2 ? null : recordNumber * 3;  // 20% chance NULL
                row[5] = "Type " + (random.nextInt(3) + 1);  // Random type 1-3
                row[6] = "Bid " + (random.nextInt(2) + 1);  // Random bid 1-2
                row[7] = random.nextDouble() < 0.2 ? null : random.nextDouble() * 10000;  // 20% chance NULL
                row[8] = random.nextDouble() < 0.2 ? null : random.nextDouble() * 10000;  // 20% chance NULL
                row[9] = random.nextDouble() * 100;  // pl1
                row[10] = random.nextDouble() * 100;  // pl2
                row[11] = random.nextDouble() * 50;  // gain_dx
                row[12] = random.nextDouble() * 50;  // sx_px
                row[13] = random.nextDouble() * 50;  // x99_out
                row[14] = recordNumber % 100;  // batch
                row[15] = random.nextDouble() < 0.2 ? null : LocalDate.now().minusDays(recordNumber % 365);  // 20% chance NULL
                row[16] = random.nextDouble() < 0.2 ? null : (recordNumber % 2) == 0;  // 20% chance NULL

                batchArgs.add(row);
            }

            // Execute batch insert
            jdbcTemplate.batchUpdate(sql, batchArgs);

            // Progress indicator
            if ((batch + 1) % 10 == 0) {
                System.out.println("Inserted " + ((batch + 1) * batchSize) + " records...");
            }
        }
    }
}