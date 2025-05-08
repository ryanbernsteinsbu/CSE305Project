package dao;

import model.Customer;
import model.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

	private static final String URL = "jdbc:mysql://localhost:3306/CSE305?useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "root";
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public String addCustomer(Customer c) {
		return tryUpdate(() -> insert(c));
	}

	public String editCustomer(Customer c) {
		return tryUpdate(() -> update(c));
	}

	public String deleteCustomer(String id) {
		return tryUpdate(() -> delete(Long.parseLong(id)));
	}

	public Customer getCustomer(String id) {
		return tryQuery(() -> find(Long.parseLong(id)));
	}

	public List<Customer> getCustomers(String kw) {
		return tryQuery(() -> search(kw));
	}

	public List<Customer> getAllCustomers() {
		return tryQuery(() -> search(""));
	}

	public List<Customer> getCustomerMailingList() {
		return tryQuery(this::mailingList);
	}

	public String getCustomerID(String email) {
		return tryQuery(() -> idFromEmail(email));
	}

	public Customer getHighestRevenueCustomer() {
		return tryQuery(this::highestRated);
	}

	private int insert(Customer c) throws SQLException {
		String sql = "INSERT INTO Customers " + "(CustomerID,FirstName,LastName,Address,City,State,ZipCode,"
				+ "Telephone,EmailAddress,AccountCreationDate,CreditCardNumber,Rating)"
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection cn = conn(); PreparedStatement ps = cn.prepareStatement(sql)) {
			fill(ps, c);
			ps.setInt(12, c.getRating());
			return ps.executeUpdate();
		}
	}

	private int update(Customer c) throws SQLException {
		String sql = "UPDATE Customers SET FirstName=?,LastName=?,Address=?,City=?,State=?,ZipCode=?,"
				+ "Telephone=?,EmailAddress=?,CreditCardNumber=?,Rating=? WHERE CustomerID=?";
		try (Connection cn = conn(); PreparedStatement ps = cn.prepareStatement(sql)) {
			fill(ps, c);
			ps.setInt(10, c.getRating());
			ps.setLong(11, c.getCustomerID());
			return ps.executeUpdate();
		}
	}

	private void fill(PreparedStatement ps, Customer c) throws SQLException {
		ps.setLong(1, c.getCustomerID());
		ps.setString(2, c.getFirstName());
		ps.setString(3, c.getLastName());
		ps.setString(4, c.getAddress());
		ps.setString(5, c.getLocation().getCity());
		ps.setString(6, c.getLocation().getState());
		ps.setString(7, c.getLocation().getZipCode());
		ps.setString(8, c.getTelephone());
		ps.setString(9, c.getEmail());
		ps.setDate(10, toSql(c.getAccountCreationDate()));
		ps.setString(11, c.getCreditCard());
	}

	private int delete(long id) throws SQLException {
		try (Connection c = conn();
				PreparedStatement ps = c.prepareStatement("DELETE FROM Customers WHERE CustomerID=?")) {
			ps.setLong(1, id);
			return ps.executeUpdate();
		}
	}

	private Customer find(long id) throws SQLException {
		try (Connection c = conn();
				PreparedStatement ps = c.prepareStatement("SELECT * FROM Customers WHERE CustomerID=?")) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	private List<Customer> search(String kw) throws SQLException {
		String like = "%" + (kw == null ? "" : kw) + "%";
		String sql = "SELECT * FROM Customers WHERE FirstName LIKE ? OR LastName LIKE ? OR EmailAddress LIKE ?";
		try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, like);
			ps.setString(2, like);
			ps.setString(3, like);
			try (ResultSet rs = ps.executeQuery()) {
				List<Customer> out = new ArrayList<>();
				while (rs.next())
					out.add(map(rs));
				return out;
			}
		}
	}

	private List<Customer> mailingList() throws SQLException {
		List<Customer> out = new ArrayList<>();
		try (Connection c = conn();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery("SELECT EmailAddress FROM Customers")) {
			while (rs.next()) {
				Customer tmp = new Customer();
				tmp.setEmail(rs.getString(1));
				out.add(tmp);
			}
		}
		return out;
	}

	private String idFromEmail(String email) throws SQLException {
		try (Connection c = conn();
				PreparedStatement ps = c
						.prepareStatement("SELECT CustomerID FROM Customers WHERE EmailAddress=? LIMIT 1")) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? String.valueOf(rs.getLong(1)) : "-1";
			}
		}
	}

	private Customer highestRated() throws SQLException {
		try (Connection c = conn();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Customers ORDER BY Rating DESC LIMIT 1")) {
			return rs.next() ? map(rs) : null;
		}
	}

	private Customer map(ResultSet r) throws SQLException {
		Customer c = new Customer();
		Location l = new Location();
		c.setCustomerID(r.getLong("CustomerID"));
		c.setFirstName(r.getString("FirstName"));
		c.setLastName(r.getString("LastName"));
		c.setAddress(r.getString("Address"));
		l.setCity(r.getString("City"));
		l.setState(r.getString("State"));
		l.setZipCode(r.getString("ZipCode"));
		c.setLocation(l);
		c.setTelephone(r.getString("Telephone"));
		c.setEmail(r.getString("EmailAddress"));
		c.setAccountCreationDate(r.getDate("AccountCreationDate"));
		c.setCreditCard(r.getString("CreditCardNumber"));
		c.setRating(r.getInt("Rating"));
		return c;
	}

	private static Connection conn() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	private static java.sql.Date toSql(java.util.Date u) {
		return u == null ? null : new java.sql.Date(u.getTime());
	}

	private interface Exec {
		int run() throws SQLException;
	}

	private interface Sup<T> {
		T get() throws SQLException;
	}

	private String tryUpdate(Exec e) {
		try {
			return e.run() > 0 ? "success" : "failure";
		} catch (SQLException x) {
			x.printStackTrace();
			return "failure";
		}
	}

	private <T> T tryQuery(Sup<T> s) {
		try {
			return s.get();
		} catch (SQLException x) {
			x.printStackTrace();
			return null;
		}
	}
}
