package main;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.ValidationException;
import javafx.collections.ObservableList;
import main.StaffDashboard.ReviewerEntry;
import main.StaffDashboard.UserEntry;

class StaffTesting {
	
	private final User staff = new User("Staff", "password", "Staff");
	private static Questions questions;
	private static Answers answers;
	private static Reviews reviews;
	private static User userA, userB, userC, userD, userE, reviewerA, reviewerB, reviewerC;
	private static Question q1, q2, q3, q4, q5, q6;

	/**
	 * Setup method runs before all other tests. Resets and Initializes test data into the database.
	 * 
	 * 
	 * @throws SQLException if SQL queries are invalid
	 * @throws ValidationException if Question or Answer is invalid
	 * @see DatabaseManager
	 */
	@BeforeAll
	static void setup() throws SQLException, ValidationException {
		//Resets all data tables in database.
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS users");
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS questions");
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS answers");
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS reviews");
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS trusted_reviewers");
		DatabaseManager.getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS staff_flags");
		DatabaseManager.initializeDatabase();
		
		//constructors for manager classes
		questions = new Questions();
		answers = new Answers();
		reviews = new Reviews();
		
		//initialize test users
		userA = new User("UserA", "password", "Staff");     
		userB = new User("UserB", "password", "Student");     
		userC = new User("UserC", "password", "Student");     
		userD = new User("UserD", "password", "Instructor");
		userE = new User("UserE", "password", "Student");   
		
		//initialize test reviewers
		reviewerA = new User("ReviewerA", "password", "Reviewer");
		reviewerB = new User("ReviewerB", "password", "Reviewer");
		reviewerC = new User("ReviewerC", "password", "Reviewer");
		
		//add all users to database
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('UserA', 'password', 'Staff')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('UserB', 'password', 'Student')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('UserC', 'password', 'Student')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('UserD', 'password', 'Instructor')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('UserE', 'password', 'Student')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('ReviewerA', 'password', 'Reviewer')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('ReviewerB', 'password', 'Reviewer')");
		DatabaseManager.getConnection().createStatement().executeUpdate(
				"INSERT INTO users (username, password, role) VALUES ('ReviewerC', 'password', 'Reviewer')");
		
		//adds all test questions to database
		questions.createQuestion(q1 = new Question(0, "Test Question 1", userB.getUsername()));
		questions.createQuestion(q2 = new Question(0, "Test Question 2", userB.getUsername()));
		questions.createQuestion(q3 = new Question(0, "Test Question 3", userE.getUsername()));
		questions.createQuestion(q4 = new Question(0, "Test Question 4", userE.getUsername()));
		questions.createQuestion(q5 = new Question(0, "Test Question 5", userB.getUsername()));
		questions.createQuestion(q6 = new Question(0, "Test Question 6", userC.getUsername()));
		
		//adds all test answers to database
		answers.createAnswer(new Answer(0, q1.getId(), "Test Answer", userA.getUsername()));
		answers.createAnswer(new Answer(0, q1.getId(), "Test Answer", userE.getUsername()));
		answers.createAnswer(new Answer(0, q2.getId(), "Test Answer", userD.getUsername()));
		answers.createAnswer(new Answer(0, q2.getId(), "Test Answer", userC.getUsername()));
		answers.createAnswer(new Answer(0, q3.getId(), "Test Answer", userA.getUsername()));
		answers.createAnswer(new Answer(0, q4.getId(), "Test Answer", userB.getUsername()));
		answers.createAnswer(new Answer(0, q4.getId(), "Test Answer", userC.getUsername()));
		answers.createAnswer(new Answer(0, q4.getId(), "Test Answer", userD.getUsername()));
		answers.createAnswer(new Answer(0, q5.getId(), "Test Answer", userA.getUsername()));
		answers.createAnswer(new Answer(0, q5.getId(), "Test Answer", userC.getUsername()));
		answers.createAnswer(new Answer(0, q6.getId(), "Test Answer", userA.getUsername()));
		answers.createAnswer(new Answer(0, q6.getId(), "Test Answer", userD.getUsername()));
		
		//adds all test reviews to database
		reviews.createReview(0, reviewerA.getUsername(), "Test Review", 0);
		reviews.createReview(1, reviewerA.getUsername(), "Test Review", 0);
		reviews.createReview(0, reviewerB.getUsername(), "Test Review", 0);
		reviews.createReview(0, reviewerC.getUsername(), "Test Review", 0);
		reviews.createReview(1, reviewerC.getUsername(), "Test Review", 0);
		reviews.createReview(2, reviewerC.getUsername(), "Test Review", 0);
		
		//adds all test trusted reviewers to database
		TrustedReviewers.trustReviewer(userA.getUsername(), reviewerA.getUsername());
		TrustedReviewers.trustReviewer(userA.getUsername(), reviewerB.getUsername());
		TrustedReviewers.trustReviewer(userA.getUsername(), reviewerC.getUsername());
		TrustedReviewers.trustReviewer(userE.getUsername(), reviewerB.getUsername());
		TrustedReviewers.trustReviewer(userD.getUsername(), reviewerA.getUsername());
	}
	
	/**
	 * Closes the Database connection once tests are finished running.
	 * 
	 * @throws SQLException if database fails to close
	 * @see DatabaseManager
	 */
	@AfterAll
	static void close() throws SQLException {
		DatabaseManager.getConnection().close();
	}
	
	/**
	 * Tests the functionality of creating and verifying the staff dashboard for staff and non-staff members.
	 * 
	 */
	@Test
	void Test_StaffCreation() {
		
		//Are valid staff members, should not throw error
		assertDoesNotThrow(() -> new StaffDashboard(staff, null, null, null));
		assertDoesNotThrow(() -> new StaffDashboard(userA, null, null, null));
		
		//Are invalid staff members, should throw error
		assertThrows(ValidationException.class, () -> new StaffDashboard(userB, null, null, null));
		assertThrows(ValidationException.class, () -> new StaffDashboard(userC, null, null, null));
		assertThrows(ValidationException.class, () -> new StaffDashboard(userD, null, null, null));
		assertThrows(ValidationException.class, () -> new StaffDashboard(userE, null, null, null));
	}
	
	/**
	 * Tests the functionality and validity of retrieving user statistics from the database.
	 * 
	 * @throws ValidationException if user is not staff
	 */
	@Test
	void Test_UserStats() throws ValidationException {
		StaffDashboard dashboard = new StaffDashboard(staff, questions, answers, reviews);
		ObservableList<UserEntry> output = dashboard.getUserStatistics();
		
		//UserA asks 0 questions and gives 4 answers
		assertEquals(output.get(3).name, "UserA");
		assertEquals(output.get(3).questionsAsked, 0);
		assertEquals(output.get(3).answersGiven, 4);
		
		//UserB asks 3 questions and gives 1 answer
		assertEquals(output.get(4).name, "UserB");
		assertEquals(output.get(4).questionsAsked, 3);
		assertEquals(output.get(4).answersGiven, 1);
		
		//UserC asks 1 question and gives 3 answers
		assertEquals(output.get(5).name, "UserC");
		assertEquals(output.get(5).questionsAsked, 1);
		assertEquals(output.get(5).answersGiven, 3);
		
		//UserD asks 0 questions and gives 3 answers
		assertEquals(output.get(6).name, "UserD");
		assertEquals(output.get(6).questionsAsked, 0);
		assertEquals(output.get(6).answersGiven, 3);
		
		//UserE asks 2 questions and gives 1 answer
		assertEquals(output.get(7).name, "UserE");
		assertEquals(output.get(7).questionsAsked, 2);
		assertEquals(output.get(7).answersGiven, 1);
	}
	
	
	/**
	 * Tests the functionality and validity of retrieving reviewer statistics from the database.
	 * 
	 * @throws ValidationException if user is not staff
	 * @throws SQLException if SQL queries are invalid
	 */
	@Test
	void Test_ReviewerStats() throws ValidationException, SQLException {
		StaffDashboard dashboard = new StaffDashboard(staff, questions, answers, reviews);
		
		ObservableList<ReviewerEntry> output = dashboard.getReviewerStatistics();
		
		//ReviewerA has given 2 reviews and is trusted by 2 users
		assertEquals(output.get(0).name, "ReviewerA");
		assertEquals(output.get(0).reviewsGiven, 2);
		assertEquals(output.get(0).trusts, 2);
		
		//ReviewerB has given 1 review and is trusted by 2 users
		assertEquals(output.get(1).name, "ReviewerB");
		assertEquals(output.get(1).reviewsGiven, 1);
		assertEquals(output.get(1).trusts, 2);
		
		//ReviewerC has given 3 reviews and is trusted by 1 user
		assertEquals(output.get(2).name, "ReviewerC");
		assertEquals(output.get(2).reviewsGiven, 3);
		assertEquals(output.get(2).trusts, 1);
	}
	
	/**
	 * Tests the ability to flag and clear flags from users by a staff member.
	 * 
	 * @throws ValidationException if user is not staff
	 */
	@Test 
	void Test_flagUsers() throws ValidationException {
		StaffDashboard dashboard = new StaffDashboard(staff, questions, answers, reviews);
		
		//Flags user A with two flags
		dashboard.flagUser(userA, "Test Flag 1");
		dashboard.flagUser(userA, "Test Flag 2");
		
		//Both flags should appear in resulting list
		assertEquals(dashboard.getFlags(userA).size(), 2);
		assertEquals(dashboard.getFlags(userA).get(0).getKey(), "Test Flag 1");
		assertEquals(dashboard.getFlags(userA).get(0).getValue(), staff.getUsername());
		
		//Clears flags from UserA
		dashboard.clearFlags(userA);
		
		//UserA should have no flags now
		assertTrue(dashboard.getFlags(userA).isEmpty());
	}

}
