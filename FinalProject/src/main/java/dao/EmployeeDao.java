package dao;

import model.Employee;
import model.Location;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

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

	public String addEmployee(Employee e) {
		return tryUpdate(() -> insert(e));
	}

	public String editEmployee(Employee e) {
		return tryUpdate(() -> update(e));
	}

	public String deleteEmployee(String id) {
		return tryUpdate(() -> delete(Long.parseLong(id)));
	}

	public Employee getEmployee(String id) {
		return tryQuery(() -> find(Long.parseLong(id)));
	}

	public List<Employee> getEmployees() {
		return tryQuery(this::all);
	}

	public Employee getHighestRevenueEmployee() {
		return tryQuery(this::highestRevenue);
	}

	public String getEmployeeID(String email) {
		return tryQuery(() -> idFromEmail(email));
	}

	private int insert(Employee e) throws SQLException {
		String sql = "INSERT INTO Employee " + "(EmployeeID,FirstName,LastName,Address,City,State,ZipCode,"
				+ "Telephone,StartDate,HourlyRate,SSN) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
			fillStd(e, ps);
			ps.setBigDecimal(10, nz(e.getHourlyRate()));
			ps.setString(11, e.getSsn());
			return ps.executeUpdate();
		}
	}

	private int update(Employee e) throws SQLException {
		String sql = "UPDATE Employee SET FirstName=?,LastName=?,Address=?,City=?,State=?,ZipCode=?,"
				+ "Telephone=?,StartDate=?,HourlyRate=?,SSN=? WHERE EmployeeID=?";
		try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
			fillStd(e, ps);
			ps.setBigDecimal(9, nz(e.getHourlyRate()));
			ps.setString(10, e.getSsn());
			ps.setLong(11, e.getEmployeeID());
			return ps.executeUpdate();
		}
	}

	private void fillStd(Employee e, PreparedStatement ps) throws SQLException {
		ps.setLong(1, e.getEmployeeID());
		ps.setString(2, e.getFirstName());
		ps.setString(3, e.getLastName());
		ps.setString(4, e.getAddress());
		ps.setString(5, e.getLocation().getCity());
		ps.setString(6, e.getLocation().getState());
		ps.setInt(7, e.getLocation().getZipCodeInt());
		ps.setString(8, e.getTelephone());
		ps.setDate(9, toSql(e.getStartDate()));
	}

	private int delete(long id) throws SQLException {
		try (Connection c = conn();
				PreparedStatement ps = c.prepareStatement("DELETE FROM Employee WHERE EmployeeID=?")) {
			ps.setLong(1, id);
			return ps.executeUpdate();
		}
	}

	private Employee find(long id) throws SQLException {
		try (Connection c = conn();
				PreparedStatement ps = c.prepareStatement("SELECT * FROM Employee WHERE EmployeeID=?")) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? map(rs) : null;
			}
		}
	}

	private List<Employee> all() throws SQLException {
		List<Employee> out = new ArrayList<>();
		try (Connection c = conn();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Employee")) {
			while (rs.next())
				out.add(map(rs));
		}
		return out;
	}

	private Employee highestRevenue() throws SQLException {
		try (Connection c = conn();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Employee ORDER BY HourlyRate DESC LIMIT 1")) {
			return rs.next() ? map(rs) : null;
		}
	}

	private String idFromEmail(String email) throws SQLException {
		String local = email.split("@")[0].toLowerCase();
		String sql = "SELECT EmployeeID FROM Employee " + "WHERE LOWER(CONCAT(LEFT(FirstName,1),LastName))=? "
				+ "   OR LOWER(CONCAT(FirstName,'.',LastName))=? LIMIT 1";
		try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, local);
			ps.setString(2, local);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? String.valueOf(rs.getLong(1)) : "-1";
			}
		}
	}

	private Employee map(ResultSet r) throws SQLException {
		Employee e = new Employee();
		Location l = new Location();
		e.setEmployeeID(r.getLong("EmployeeID"));
		e.setFirstName(r.getString("FirstName"));
		e.setLastName(r.getString("LastName"));
		e.setAddress(r.getString("Address"));
		l.setCity(r.getString("City"));
		l.setState(r.getString("State"));
		l.setZipCode(r.getString("ZipCode"));
		e.setLocation(l);
		e.setTelephone(r.getString("Telephone"));
		e.setStartDate(r.getDate("StartDate"));
		e.setHourlyRate(r.getBigDecimal("HourlyRate"));
		e.setSsn(r.getString("SSN"));
		return e;
	}

	private static Connection conn() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	private static java.sql.Date toSql(java.util.Date u) {
		return u == null ? null : new java.sql.Date(u.getTime());
	}

	private static BigDecimal nz(BigDecimal b) {
		return b == null ? BigDecimal.ZERO : b;
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

	private <T> T tryQuery(Sup<T> q) {
		try {
			return q.get();
		} catch (SQLException x) {
			x.printStackTrace();
			return null;
		}
	}
}
