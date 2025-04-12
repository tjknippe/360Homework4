package main;
/*******
 * <p> Title: DatabaseManager Class. </p>
 * 
 * <p> Description: Class that initializes the database. </p>
 * 
 * @author Jacqui Person
 * 
 */
import java.sql.*;

public class DatabaseManager {
	/** Database URL */
    public static String DB_URL = "jdbc:h2:./data/testserver;AUTO_SERVER=TRUE";
    /** Database username */
    private static final String DB_USERNAME = "sa";
    /** Database password */
    private static final String DB_PASSWORD = "";

	/**
	* Attempts to establish connection to database URL DB_URL. 
	*
	* @return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD) the driver for the connection 
	* 
	*/
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    /**
	* Attempts to initialize the database and prints whether or not it was successful. 
	*/
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    text VARCHAR(1000),
                    student_id VARCHAR(255)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS answers (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    question_id INT,
                    text VARCHAR(1000),
                    student_id VARCHAR(255)
                );
            """);

            stmt.executeUpdate("DROP TABLE IF EXISTS reviews");
            
            stmt.executeUpdate("""
            	    CREATE TABLE IF NOT EXISTS reviews (
            		    id INT AUTO_INCREMENT PRIMARY KEY,
            	        answer_id INT,
            	        reviewer VARCHAR(255),  
            	        text VARCHAR(1000),
            	        weightage INT
            	    );
            	""");

            stmt.executeUpdate("""
            	    CREATE TABLE IF NOT EXISTS users (
            	        username VARCHAR(255) PRIMARY KEY,
            	        password VARCHAR(255),
            	        role VARCHAR(50)
            	    );
            	""");
            
            stmt.executeUpdate("""
            	    CREATE TABLE IF NOT EXISTS trusted_reviewers (
            	        student VARCHAR,
            	        reviewer VARCHAR
            	    );
            	""");
            
            stmt.executeUpdate("""
            		CREATE TABLE IF NOT EXISTS reviewer_requests (
            			student_username VARCHAR(255),
            			status VARCHAR(20),  -- 'pending', 'accepted', 'rejected'
            			PRIMARY KEY (student_username)
            		);
            	 """);
            
            stmt.executeUpdate("""
            		CREATE TABLE IF NOT EXISTS staff_flags (
            			staff_username VARCHAR(255),
            			flagged_user VARCHAR(255),
            			staff_comment VARCHAR(1000)
            			
            		);
            		""");

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            System.out.println("Failed to initialize database: " + e.getMessage());
        }
    }
    
    /**
   	* Creates the table necessary for private feedback between a student and a 
   	* reviewer. 
   	*/
    public static void createPrivateFeedbackTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS private_feedback (
                id INT AUTO_INCREMENT PRIMARY KEY,
                reviewer VARCHAR,
                student VARCHAR,
                reviewId INT,
                feedbackText VARCHAR
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
