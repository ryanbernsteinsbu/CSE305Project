package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import model.Location;

public class CustomerDao {

    private static final String URL  = "jdbc:mysql://localhost:3306/CSE305";
    private static final String USER = "root";
    private static final String PASS = "root";


    public int add(Customer c) throws SQLException {
        String q = """
            INSERT INTO customers
            (customerID, firstName, lastName, address, city, state, zipCode,
             telephone, email, accountCreationDate, creditCard, rating)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)""";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setLong  (1,  c.getCustomerID());
            ps.setString(2,  c.getFirstName());
            ps.setString(3,  c.getLastName());
            ps.setString(4,  c.getAddress());
            ps.setString(5,  c.getLocation().getCity());
            ps.setString(6,  c.getLocation().getState());
            ps.setLong(7,  c.getLocation().getZipCode());
            ps.setString(8,  c.getTelephone());
            ps.setString(9,  c.getEmail());
            ps.setDate  (10, new java.sql.Date(c.getAccountCreationDate().getTime()));
            ps.setString(11, c.getCreditCard());
            ps.setInt   (12, c.getRating());
            return ps.executeUpdate();           // 1 = success, 0 = fail
        }
    }

    public int update(Customer c) throws SQLException {
        String q = """
            UPDATE customers SET firstName=?, lastName=?, address=?, city=?, state=?, zipCode=?,
                                 telephone=?, email=?, creditCard=?, rating=?
            WHERE customerID=?""";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setString(1,  c.getFirstName());
            ps.setString(2,  c.getLastName());
            ps.setString(3,  c.getAddress());
            ps.setString(4,  c.getLocation().getCity());
            ps.setString(5,  c.getLocation().getState());
            ps.setLong(6,  c.getLocation().getZipCode());
            ps.setString(7,  c.getTelephone());
            ps.setString(8,  c.getEmail());
            ps.setString(9,  c.getCreditCard());
            ps.setInt   (10, c.getRating());
            ps.setLong  (11, c.getCustomerID());
            return ps.executeUpdate();
        }
    }

    public int delete(long id) throws SQLException {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement("DELETE FROM customers WHERE customerID=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    public Customer find(long id) throws SQLException {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM customers WHERE customerID=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Customer> search(String keyword) throws SQLException {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        String q = "SELECT * FROM customers WHERE firstName LIKE ? OR lastName LIKE ? OR email LIKE ?";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            List<Customer> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    private Customer map(ResultSet r) throws SQLException {
        Customer c = new Customer();
        Location  l = new Location();

        c.setCustomerID(r.getLong("customerID"));
        c.setFirstName (r.getString("firstName"));
        c.setLastName  (r.getString("lastName"));
        c.setAddress   (r.getString("address"));

        l.setCity  (r.getString("city"));
        l.setState (r.getString("state"));
        l.setZipCode(r.getString("zipCode"));
        c.setLocation(l);

        c.setTelephone (r.getString("telephone"));
        c.setEmail     (r.getString("email"));
        c.setAccountCreationDate(r.getDate("accountCreationDate"));
        c.setCreditCard(r.getString("creditCard"));
        c.setRating    (r.getInt("rating"));
        return c;
    }
}
