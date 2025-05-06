package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Employee;
import model.Location;


public class EmployeeDao {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/demo";
	private static final String JDBC_USER = "root";
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

	private Employee mapRow(ResultSet rs) throws SQLException {
		Employee e = new Employee();
		Location loc = new Location();

		e.setId(rs.getString("id"));
		e.setFirstName(rs.getString("first_name"));
		e.setLastName(rs.getString("last_name"));
		e.setEmail(rs.getString("email"));
		e.setAddress(rs.getString("address"));
		e.setTelephone(rs.getString("telephone"));
		e.setEmployeeID(rs.getString("employee_id"));
		e.setStartDate(rs.getString("start_date"));
		e.setHourlyRate(rs.getDouble("hourly_rate"));

		loc.setCity(rs.getString("city"));
		loc.setState(rs.getString("state"));
		loc.setZipCode(rs.getInt("zip_code"));
		e.setLocation(loc);

		return e;
	}


	public String addEmployee(Employee employee) {
		final String sql = "INSERT INTO employee (id, first_name, last_name, email, address, city, state, zip_code, telephone, employee_id, start_date, hourly_rate) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, employee.getId());
			ps.setString(2, employee.getFirstName());
			ps.setString(3, employee.getLastName());
			ps.setString(4, employee.getEmail());
			ps.setString(5, employee.getAddress());
			ps.setString(6, employee.getLocation().getCity());
			ps.setString(7, employee.getLocation().getState());
			ps.setInt(8, employee.getLocation().getZipCode());
			ps.setString(9, employee.getTelephone());
			ps.setString(10, employee.getEmployeeID());
			ps.setString(11, employee.getStartDate());
			ps.setDouble(12, employee.getHourlyRate());

			return ps.executeUpdate() == 1 ? "success" : "failure";
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "failure";
		}
	}

	public String editEmployee(Employee employee) {
		final String sql = "UPDATE employee SET first_name = ?, last_name = ?, email = ?, address = ?, city = ?, state = ?, zip_code = ?, telephone = ?, start_date = ?, hourly_rate = ? WHERE employee_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, employee.getFirstName());
			ps.setString(2, employee.getLastName());
			ps.setString(3, employee.getEmail());
			ps.setString(4, employee.getAddress());
			ps.setString(5, employee.getLocation().getCity());
			ps.setString(6, employee.getLocation().getState());
			ps.setInt(7, employee.getLocation().getZipCode());
			ps.setString(8, employee.getTelephone());
			ps.setString(9, employee.getStartDate());
			ps.setDouble(10, employee.getHourlyRate());
			ps.setString(11, employee.getEmployeeID());

			return ps.executeUpdate() == 1 ? "success" : "failure";
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "failure";
		}
	}

	public String deleteEmployee(String employeeID) {
		final String sql = "DELETE FROM employee WHERE employee_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, employeeID);
			return ps.executeUpdate() == 1 ? "success" : "failure";
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "failure";
		}
	}

	public List<Employee> getEmployees() {
		final String sql = "SELECT * FROM employee";
		List<Employee> list = new ArrayList<>();
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

	public Employee getEmployee(String employeeID) {
		final String sql = "SELECT * FROM employee WHERE employee_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, employeeID);
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

	public Employee getHighestRevenueEmployee() {
		final String sql = "SELECT e.*, SUM(o.total) AS revenue "
				+ "FROM employee e JOIN orders o ON e.employee_id = o.employee_id "
				+ "GROUP BY e.employee_id ORDER BY revenue DESC LIMIT 1";
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

	public String getEmployeeID(String username) {
		final String sql = "SELECT employee_id FROM employee WHERE email = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("employee_id");
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Employee getDummyEmployee() {
		Employee employee = new Employee();
		Location location = new Location();
		location.setCity("Stony Brook");
		location.setState("NY");
		location.setZipCode(11790);

		employee.setEmail("shiyong@cs.sunysb.edu");
		employee.setFirstName("Shiyong");
		employee.setLastName("Lu");
		employee.setLocation(location);
		employee.setAddress("123 Success Street");
		employee.setStartDate("2006-10-17");
		employee.setTelephone("5166328959");
		employee.setEmployeeID("631-413-5555");
		employee.setHourlyRate(100);

		return employee;
	}

	public List<Employee> getDummyEmployees() {
		List<Employee> employees = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			employees.add(getDummyEmployee());
		}
		return employees;
	}
}
