package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.ValidationException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Pair;

public class StaffDashboard {
	/**
	 * Class Struct for storing return data of each user database entry
	 * 
	 * 
	 * 
	 */
	public class UserEntry {
		public String name;
		public int questionsAsked;
		public int answersGiven;
		
		public UserEntry(String n, int q, int a) {
			name = n;
			questionsAsked = q;
			answersGiven = a;
		}
	}
	/**
	 * Class Struct for storing return data of each reviewer database entry.
	 */
	public class ReviewerEntry {
		public String name;
		public int reviewsGiven;
		public int trusts;
		public float averageScore;
		
		public ReviewerEntry(String n, int r, int t, float s) {
			name = n;
			reviewsGiven = r;
			trusts = t;
			averageScore = s;
		}
	}
	
	private User user;
	private Questions qManager;
	private Answers aManager;
	private Reviews rManager;
	
	
	/**
	 * Constructor for StaffDashboard.
	 * 
	 * 
	 * @param User the staff user
	 * @param Questions manager for Questions class
	 * @param Answers manager for Answers class
	 * @param Reviews manager for Reviews class
	 * @throws ValidationException if user is not a verified staff
	 */
	public StaffDashboard(User user, Questions q, Answers a, Reviews r) throws ValidationException {
		if(user.getRole() != "Staff") {
			throw new ValidationException("User is not Staff!");
		}
		this.user = user;
		this.qManager = q;
		this.aManager = a;
		this.rManager = r;
	}
	
	
	/**
	 * Fetch's statistics about all users from database.
	 * 
	 * 
	 * @return entries from database as ObservableList
	 * @see ObservableList
	 */
	public ObservableList<UserEntry> getUserStatistics() {
		ObservableList<UserEntry> entries = FXCollections.observableArrayList();
		try {
			ResultSet rs = DatabaseManager.getConnection().createStatement().executeQuery(
					"SELECT u.username AS name, COUNT(DISTINCT q.id) AS qTotal, COUNT(DISTINCT a.id) AS aTotal FROM users u "
					+ "LEFT JOIN questions q ON u.username = q.student_id "
					+ "LEFT JOIN answers a ON u.username = a.student_id "
					+ "GROUP BY u.username");
			
			while(rs.next()) {
				String name = rs.getString("name");
				UserEntry e = new UserEntry(name, rs.getInt("qTotal"), rs.getInt("aTotal"));
				entries.add(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	
	/**
	 * Fetch's statistics about all reviewers from database.
	 * 
	 * 
	 * @return entries from database as ObservableList
	 * @see ObservableList
	 */
	public ObservableList<ReviewerEntry> getReviewerStatistics() {
		ObservableList<ReviewerEntry> entries = FXCollections.observableArrayList();
		try {
			ResultSet rs = DatabaseManager.getConnection().createStatement().executeQuery(
					"SELECT u.username AS name, COUNT(DISTINCT r.answer_id) AS total, COUNT(DISTINCT t.student) AS trusts FROM users u "
					+ "LEFT JOIN reviews r ON u.username = r.reviewer "
					+ "LEFT JOIN trusted_reviewers t ON u.username = t.reviewer "
					+ "WHERE u.role = 'Reviewer' "
					+ "GROUP BY u.username");
			while(rs.next()) {
				String name = rs.getString("name");
				System.out.println("Name:" + name);
				int reviews = rs.getInt("total");
				int trusts = rs.getInt("trusts");
				entries.add(new ReviewerEntry(name, reviews, trusts, 0));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	/**
	 * Flag's a user with a specific comment only visible to other staff members in the database.
	 * 
	 * @param user the user to flag
	 * @param comment the comment with flag
	 */
	public void flagUser(User u, String comment) {
		try(PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement("INSERT INTO staff_flags (staff_username, flagged_user, staff_comment) VALUES (?,?,?)" )){
			stmt.setString(1, user.getUsername());
			stmt.setString(2, u.getUsername());
			stmt.setString(3, comment);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch's all flags tagged to a specific user
	 * 
	 * 
	 * @param User the user to fetch flags from
	 * @return A list of all flags as a pair of the comment and username of staff who wrote it.
	 * @see Pair
	 */
	public List<Pair<String, String>> getFlags(User u) {
		List<Pair<String, String>> flags = new ArrayList<Pair<String, String>>();
		try(PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement("SELECT * FROM staff_flags WHERE flagged_user = ?")) {
			stmt.setString(1,  u.getUsername());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				flags.add(new Pair<String, String>(rs.getString("staff_comment"), rs.getString("staff_username")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flags;
	}
	
	/**
	 * Erases all flags belonging to a user from the database.
	 * 
	 * @param User the user to clear
	 */
	public void clearFlags(User u) {
		try(PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement("DELETE FROM staff_flags WHERE flagged_user = ?")) {
			stmt.setString(1,  u.getUsername());
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
}
