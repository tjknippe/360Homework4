package main;
/*******
 * <p> Title: User Class. </p>
 * 
 * <p> Class that represents a user, including the user's username, password, and role. </p>
 * 
 * @author Jacqui Person
 * 
 */
public class User {
	/** User's username */
    private String username;
    /** User's password */
    private String password;
    /** User's role */
    private String role;
    

    /** 
	* The constructor for a User.
	* 
	* @param username the user's username
	* @param password the user's password
	* @param role the user's role
	*/
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
	* Returns the user's username.
	*
	* @return username the user's username
	* 
	*/
    public String getUsername() { return username; }
    
    /**
	* Returns the user's password.
	*
	* @return password the user's password.
	* 
	*/
    public String getPassword() { return password; }
    
    /**
	* Returns the user's role.
	*
	* @return role the user's role
	* 
	*/
    public String getRole() { return role; }
    
    /**
	* Sets the user's role.
	*
	* @param role the role to set for the user
	* 
	*/
    public void setRole(String role) { this.role = role; }
}
