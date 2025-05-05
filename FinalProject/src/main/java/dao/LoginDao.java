package dao;

import model.Login;

import java.sql.*;

public class LoginDao {
	/*
	 * This class handles all the database operations related to login functionality
	 */
	private static final String URL = "jdbc:mysql://localhost:3306/demo";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static final int PRIME_1 = 257;
    private static final long PRIME_MODULUS = 9223372036854775783L;
    private static final Boolean IS_HASH = true;
	
	public Login login(String username, String password, String role) {
		/*
		 * Return a Login object with role as "manager", "customerRepresentative" or "customer" if successful login
		 * Else, return null
		 * The role depends on the type of the user, which has to be handled in the database
		 * username, which is the email address of the user, is given as method parameter
		 * password, which is the password of the user, is given as method parameter
		 * Query to verify the username and password and fetch the role of the user, must be implemented
		 */
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD); //establish the connection with SQL svr


            String sql = "SELECT role FROM login WHERE username = ? AND password = ?"; 
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, hashPassword(password));  //set values in string

            rs = pst.executeQuery();

            if (rs.next()) {
                String dbRole = rs.getString("role");
                Login login = new Login();
                login.setUsername(username);
                login.setPassword(hashPassword(password));
                login.setRole(dbRole);
                return login;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static int getSalt(String password) {
        int salt = 0;
        for (int i = 0; i < password.length(); i++) {
            salt += password.charAt(i) * (i + 1); //create a salt based on the password
        }
        return salt;
    }
	
	public static String hashPassword(String password) {
        int salt = getSalt(password);
        long hashValue = 0;

        for (int i = 0; i < password.length(); i++) {
            hashValue = (hashValue * PRIME_1 + (password.charAt(i) + salt + i)) % PRIME_MODULUS;
        }

        return (IS_HASH) ? String.valueOf(hashValue) : password;
    }
	
	public String addUser(Login login) {
		/*
		 * Query to insert a new record for user login must be implemented
		 * login, which is the "Login" Class object containing username and password for the new user, is given as method parameter
		 * The username and password from login can get accessed using getter methods in the "Login" model
		 * e.g. getUsername() method will return the username encapsulated in login object
		 * Return "success" on successful insertion of a new user
		 * Return "failure" for an unsuccessful database operation
		 */
		
        Connection con = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD); //establish the connection with SQL svr

            String sql = "INSERT INTO login (username, password, role) VALUES (?, ?, ?)"; //prepared add to db string 
            pst = con.prepareStatement(sql);
            pst.setString(1, login.getUsername());
            pst.setString(2, hashPassword(login.getPassword()));  
            pst.setString(3, login.getRole());

            int rows = pst.executeUpdate();
            return rows > 0 ? "success" : "failure";

        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
		
	}

}
