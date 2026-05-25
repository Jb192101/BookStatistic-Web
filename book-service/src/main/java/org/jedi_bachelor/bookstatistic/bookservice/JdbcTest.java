package org.jedi_bachelor.bookstatistic.bookservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcTest {
    public static void main(String[] args) {
        // Пробуем разные комбинации
        String[][] testConfigs = {
                {"jdbc:postgresql://localhost:5491/book-postgres", "user_book_postgres", "password_book_postgres"},
                {"jdbc:postgresql://localhost:5491/postgres?sslmode=disable", "postgres", "password_book_postgres"},  // Через админа
                {"jdbc:postgresql://localhost:5491/postgres?sslmode=disable", "postgres", "postgres"},  // Стандартный пароль
                {"jdbc:postgresql://localhost:5491/user_book_postgres?sslmode=disable", "user_book_postgres", "password_book_postgres"}  // БД с именем пользователя
        };

        for (int i = 0; i < testConfigs.length; i++) {
            String[] config = testConfigs[i];
            System.out.println("\n=== Test " + (i+1) + " ===");
            System.out.println("URL: " + config[0]);
            System.out.println("User: " + config[1]);
            System.out.println("Password: " + config[2]);

            try {
                Class.forName("org.postgresql.Driver");
                try (Connection conn = DriverManager.getConnection(config[0], config[1], config[2])) {
                    System.out.println("✅ SUCCESS!");
                    System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                    break;
                }
            } catch (Exception e) {
                System.out.println("❌ FAILED: " + e.getMessage());
            }
        }
    }
}