package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Customer;
import model.Location;

public class CustomerDao {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/CSE305";
    private static final String JDBC_USER     = "root";
    private static final String JDBC_PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found; add it to the class‑path");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c   = new Customer();
        Location loc = new Location();

        c.setId(rs.getString("id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setTelephone(rs.getString("telephone"));
        c.setCreditCard(rs.getString("credit_card"));
        c.setRating(rs.getInt("rating"));

        loc.setCity(rs.getString("city"));
        loc.setState(rs.getString("state"));
        loc.setZipCode(rs.getInt("zip_code"));
        c.setLocation(loc);

        return c;
    }


    public String addCustomer(Customer customer) {
        final String sql = "INSERT INTO customer (customerID, firstName, lastName, email, address, city, state, zipCode, telephone, creditCard, rating) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getId());
            ps.setString(2, customer.getFirstName());
            ps.setString(3, customer.getLastName());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getLocation().getCity());
            ps.setString(7, customer.getLocation().getState());
            ps.setInt   (8, customer.getLocation().getZipCode());
            ps.setString(9, customer.getTelephone());
            ps.setString(10, customer.getCreditCard());
            ps.setInt   (11, customer.getRating());

            return ps.executeUpdate() == 1 ? "success" : "failure";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }

    public String editCustomer(Customer customer) {
        final String sql = "UPDATE customer SET firstName = ?, lastName = ?, email = ?, address = ?, city = ?, state = ?, zipCode = ?, telephone = ?, creditCard = ?, rating = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getLocation().getCity());
            ps.setString(6, customer.getLocation().getState());
            ps.setInt   (7, customer.getLocation().getZipCode());
            ps.setString(8, customer.getTelephone());
            ps.setString(9, customer.getCreditCard());
            ps.setInt   (10, customer.getRating());
            ps.setString(11, customer.getId());

            return ps.executeUpdate() == 1 ? "success" : "failure";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }

    public String deleteCustomer(String customerID) {
        final String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerID);
            return ps.executeUpdate() == 1 ? "success" : "failure";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }

    public List<Customer> getCustomers(String searchKeyword) {
        final String keyword = (searchKeyword == null || searchKeyword.isBlank()) ? "%" : ("%" + searchKeyword + "%");
        final String sql = "SELECT * FROM customer WHERE firstName LIKE ? OR lastName LIKE ? OR email LIKE ?";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public Customer getCustomer(String customerID) {
        final String sql = "SELECT * FROM customer WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Customer getHighestRevenueCustomer() {
        final String sql = "SELECT c.*, SUM(o.total) AS revenue " +
                           "FROM customer c JOIN orders o ON c.id = o.customer_id " +
                           "GROUP BY c.id ORDER BY revenue DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Customer> getCustomerMailingList() {
        final String sql = "SELECT * FROM customer";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<Customer> getAllCustomers() {
        return getCustomerMailingList();
    }

    public String getCustomerID(String email) {
        final String sql = "SELECT id FROM customer WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("customerID");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

	public Customer getDummyCustomer() {
		Location location = new Location();
		location.setZipCode(11790);
		location.setCity("Stony Brook");
		location.setState("NY");

		Customer customer = new Customer();
		customer.setId("111-11-1111");
		customer.setAddress("123 Success Street");
		customer.setLastName("Lu");
		customer.setFirstName("Shiyong");
		customer.setEmail("shiyong@cs.sunysb.edu");
		customer.setLocation(location);
		customer.setTelephone("5166328959");
		customer.setCreditCard("1234567812345678");
		customer.setRating(1);

		return customer;
	}

	public List<Customer> getDummyCustomerList() {
		List<Customer> customers = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			customers.add(getDummyCustomer());
		}
		return customers;
	}
}
