package com.stocktrader.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.stocktrader.model.Customer;
import com.stocktrader.model.Location;

public class CustomerDao {

	private static final String URL = "jdbc:mysql://localhost:3306/cse305?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
	private static final String USER = "stockuser";
	private static final String PASSWORD = "stockpass";

	public String addCustomer(Customer c) {
	    String custQ = "INSERT INTO customers (customerID, firstName, lastName, address, city, state, zipCode, telephone, email, accountCreationDate, creditCard, rating) "
	                 + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	    String acctQ = "INSERT INTO accounts (accountNum, customerID) VALUES (?,?)";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement psCust = cn.prepareStatement(custQ);
	         PreparedStatement psAcct = cn.prepareStatement(acctQ)) {

	        // Insert into customers
	        long custId = Long.parseLong(c.getClientId());
	        psCust.setLong(1, custId);
	        psCust.setString(2, c.getFirstName());
	        psCust.setString(3, c.getLastName());
	        psCust.setString(4, c.getAddress());
	        psCust.setString(5, c.getLocation().getCity());
	        psCust.setString(6, c.getLocation().getState());
	        psCust.setString(7, String.valueOf(c.getLocation().getZipCode()));
	        psCust.setString(8, c.getTelephone());
	        psCust.setString(9, c.getEmail());
	        psCust.setDate(10, java.sql.Date.valueOf(c.getAccountCreationTime()));
	        psCust.setString(11, c.getCreditCard());
	        psCust.setInt(12, c.getRating());

	        if (psCust.executeUpdate() <= 0) {
	            return "failure";
	        }

	        // Insert into accounts (using same ID for accountNum)
	        psAcct.setLong(1, custId);
	        psAcct.setLong(2, custId);
	        psAcct.executeUpdate();

	        // store accountNum in Customer model for later use
	        c.setAccountNumber((int) (custId));
	        return "success";

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "failure";
	    }
	}

	public String editCustomer(Customer c){
		String q = "UPDATE customers SET firstName=?, lastName=?, address=?, city=?, state=?, zipCode=?, telephone=?, email=?, creditCard=?, rating=? WHERE customerID=?";
		try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = cn.prepareStatement(q)) {

			ps.setString(1, c.getFirstName());
			ps.setString(2, c.getLastName());
			ps.setString(3, c.getAddress());
			ps.setString(4, c.getLocation().getCity());
			ps.setString(5, c.getLocation().getState());
			ps.setString (6, String.valueOf(c.getLocation().getZipCode()));
			ps.setString(7, c.getTelephone());
			ps.setString(8, c.getEmail());
			ps.setString(9, c.getCreditCard());
			ps.setInt(10, c.getRating());
			ps.setLong(11, Long.parseLong(c.getClientId()));
			return (ps.executeUpdate() > 0) ? "success" : "failure";
		} catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
	}

	public String deleteCustomer(String id){
		try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement ps = cn.prepareStatement("DELETE FROM customers WHERE customerID=?")) {
			ps.setLong(1, Long.parseLong(id));
			return (ps.executeUpdate() > 0) ? "success" : "failure";
		} catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
	}
	public Customer getHighestRevenueCustomer() {
		String q = "SELECT c.* FROM customers c JOIN ("
	             + "SELECT a.customerID, SUM(o.numShares * o.pricePerShare) AS revenue "
	             + "FROM orders o JOIN accounts a ON o.accountNum = a.accountNum "
	             + "GROUP BY a.customerID ORDER BY revenue DESC LIMIT 1"
	             + ") topCust ON c.customerID = topCust.customerID";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q);
	         ResultSet rs = ps.executeQuery()) {

	        return rs.next() ? mapCustomer(rs) : null;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public Customer getCustomer(long id) {
	    String q = "SELECT * FROM customers WHERE customerID = ?";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q)) {
	        ps.setLong(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() ? mapCustomer(rs) : null;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public Customer getCustomer(String id) {
	    try {
	        return getCustomer(Long.parseLong(id));
	    } catch (NumberFormatException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public List<Customer> getCustomers(String keyword) {
	    String like = "%" + (keyword == null ? "" : keyword) + "%";
	    String q = "SELECT * FROM customers WHERE firstName LIKE ? OR lastName LIKE ? OR email LIKE ?";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q)) {

	        ps.setString(1, like);
	        ps.setString(2, like);
	        ps.setString(3, like);
	        ResultSet rs = ps.executeQuery();
	        List<Customer> list = new ArrayList<>();
	        while (rs.next())
	            list.add(mapCustomer(rs));
	        return list;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
	
	public List<Customer> getAllCustomers() {
	    String q = "SELECT * FROM customers";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q);
	         ResultSet rs = ps.executeQuery()) {

	        List<Customer> list = new ArrayList<>();
	        while (rs.next())
	            list.add(mapCustomer(rs));
	        return list;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
	
	public List<Customer> getCustomerMailingList() {
	    String q = "SELECT * FROM customers WHERE email IS NOT NULL AND email <> ''";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q);
	         ResultSet rs = ps.executeQuery()) {

	        List<Customer> list = new ArrayList<>();
	        while (rs.next())
	            list.add(mapCustomer(rs));
	        return list;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
	
	private Customer mapCustomer(ResultSet r) {
		
	    try {
	        Customer c = new Customer();
	        Location l = new Location();

	        c.setClientId(String.valueOf(r.getLong("customerID")));
	        c.setFirstName(r.getString("firstName"));
	        c.setLastName(r.getString("lastName"));
	        c.setAddress(r.getString("address"));

	        l.setCity(r.getString("city"));
	        l.setState(r.getString("state"));
	        l.setZipCode(Integer.valueOf(r.getString("zipCode")));
	        c.setLocation(l);
	        
	        
	        c.setAccountNumber((int)getAccountNum(String.valueOf(r.getLong("customerID"))));
	        c.setTelephone(r.getString("telephone"));
	        c.setEmail(r.getString("email"));
	        c.setAccountCreationTime(r.getDate("accountCreationDate").toString());
	        c.setCreditCard(r.getString("creditCard"));
	        c.setRating(r.getInt("rating"));

	        return c;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public static long getAccountNum(String customerId) {
		String q = ""
			      + "SELECT accountNum "
			      + "  FROM accounts a "
			      + " WHERE customerID = ?";
			    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
			         PreparedStatement ps = cn.prepareStatement(q)) {

			        ps.setLong(1, Long.valueOf(customerId));
			        try (ResultSet rs = ps.executeQuery()) {
			            return rs.next()
			                 ? rs.getLong("accountNum")
			                 : -1;
			        }
			    } catch (SQLException e) {
			        e.printStackTrace();
			        return -1;
			    }
	}
	
	public String getCustomerID(String username) {
	    String q = "SELECT a.customerID FROM login l "
	             + "JOIN accounts a ON l.accountNum = a.accountNum "
	             + "WHERE l.username = ?";
	    try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = cn.prepareStatement(q)) {

	        ps.setString(1, username);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next()
	                 ? String.valueOf(rs.getLong("customerID"))
	                 : null;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
