import java.sql.*;
import java.util.Scanner;

/**
 * This class demonstrates multiple SQL injection vulnerabilities
 * WARNING: This code is intentionally vulnerable for security testing purposes
 * DO NOT use in production!
 */
public class SQLInjectionVulnerable {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password123";
    
    /**
     * Vulnerable login method - Classic SQL Injection
     * Example exploit: username = "admin' OR '1'='1" password = "anything"
     */
    public boolean vulnerableLogin(String username, String password) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: Direct string concatenation
            String query = "SELECT * FROM users WHERE username = '" + username + 
                          "' AND password = '" + password + "'";
            
            System.out.println("Executing query: " + query);
            ResultSet rs = stmt.executeQuery(query);
            
            return rs.next(); // Returns true if user found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt);
        }
    }
    
    /**
     * Vulnerable search method - SQL Injection in LIKE clause
     * Example exploit: searchTerm = "%' OR '1'='1"
     */
    public void vulnerableSearch(String searchTerm) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: User input directly in LIKE clause
            String query = "SELECT * FROM products WHERE name LIKE '%" + searchTerm + "%'";
            
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                System.out.println("Product: " + rs.getString("name"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt);
        }
    }
    
    /**
     * Vulnerable delete method - SQL Injection in DELETE statement
     * Example exploit: userId = "1 OR 1=1" (deletes all records!)
     */
    public void vulnerableDelete(String userId) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: Can delete all records
            String query = "DELETE FROM users WHERE id = " + userId;
            
            int rowsAffected = stmt.executeUpdate(query);
            System.out.println("Deleted " + rowsAffected + " rows");
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt);
        }
    }
    
    /**
     * Vulnerable update method - SQL Injection in UPDATE statement
     * Example exploit: email = "hacker@evil.com' WHERE '1'='1"
     */
    public void vulnerableUpdate(String userId, String email) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: Can update all records
            String query = "UPDATE users SET email = '" + email + 
                          "' WHERE id = " + userId;
            
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt);
        }
    }
    
    /**
     * Vulnerable ORDER BY - SQL Injection in sorting
     * Example exploit: sortColumn = "name; DROP TABLE users--"
     */
    public void vulnerableSort(String sortColumn) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: Allows arbitrary SQL in ORDER BY
            String query = "SELECT * FROM users ORDER BY " + sortColumn;
            
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt);
        }
    }
    
    /**
     * Helper method to close database resources
     */
    private void closeResources(Connection conn, Statement stmt) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Main method demonstrating the vulnerabilities
     */
    public static void main(String[] args) {
        SQLInjectionVulnerable app = new SQLInjectionVulnerable();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SQL Injection Vulnerability Demo ===");
        System.out.println("WARNING: This is for educational purposes only!");
        
        // Demo vulnerable login
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        if (app.vulnerableLogin(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed!");
        }
        
        scanner.close();
    }
}

