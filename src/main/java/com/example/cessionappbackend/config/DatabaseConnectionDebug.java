package com.example.cessionappbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Configuration
public class DatabaseConnectionDebug {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionDebug.class);

    @Bean
    public CommandLineRunner debugDatabaseConnection(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            logger.info("Starting database connection debug...");
            
            try {
                // Test basic connection
                logger.info("Attempting to get connection from DataSource...");
                try (Connection connection = dataSource.getConnection()) {
                    logger.info("Successfully obtained connection!");
                    
                    // Get database metadata
                    DatabaseMetaData metaData = connection.getMetaData();
                    logger.info("Database Product Name: {}", metaData.getDatabaseProductName());
                    logger.info("Database Product Version: {}", metaData.getDatabaseProductVersion());
                    logger.info("Driver Name: {}", metaData.getDriverName());
                    logger.info("Driver Version: {}", metaData.getDriverVersion());
                    logger.info("URL: {}", metaData.getURL());
                    logger.info("User Name: {}", metaData.getUserName());
                    
                    // Test simple query
                    logger.info("Testing simple query...");
                    String result = jdbcTemplate.queryForObject("SELECT version()", String.class);
                    logger.info("Database version query result: {}", result);
                }
            } catch (SQLException e) {
                logger.error("Database connection failed!", e);
                logger.error("Error Code: {}", e.getErrorCode());
                logger.error("SQL State: {}", e.getSQLState());
                logger.error("Error Message: {}", e.getMessage());
                
                // Log the full stack trace
                logger.error("Full stack trace:", e);
                
                // Check if it's a connection issue
                if (e.getMessage().contains("password")) {
                    logger.error("This appears to be an authentication issue. Please check your database credentials.");
                } else if (e.getMessage().contains("connection")) {
                    logger.error("This appears to be a connection issue. Please check if the database is accessible.");
                }
            }
        };
    }
} 