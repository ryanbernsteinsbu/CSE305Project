package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import model.Location;

public class EmployeeDao {

    private static final String URL  = "jdbc:mysql://localhost:3306/CSE305";
    private static final String USER = "root";
    private static final String PASS = "root";


    public int add(Employee e) throws SQLException {
        String q = """
            INSERT INTO employee
            (employeeID, firstName, lastName, address, city, state, zipCode,
             telephone, startDate, hourlyRate, SSN)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)""";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setNString      (1,  e.getEmployeeID());
            ps.setString    (2,  e.getFirstName());
            ps.setString    (3,  e.getLastName());
            ps.setString    (4,  e.getAddress());
            ps.setString    (5,  e.getLocation().getCity());
            ps.setString    (6,  e.getLocation().getState());
            ps.setLong    (7,  e.getLocation().getZipCode());
            ps.setString    (8,  e.getTelephone());
            ps.setDate      (9,  new java.sql.Date(e.getStartDate().getTime()));
            ps.setBigDecimal(10, e.getHourlyRate());
            ps.setString    (11, e.getSsn());
            return ps.executeUpdate();
        }
    }

    public int update(Employee e) throws SQLException {
        String q = """
            UPDATE employee SET firstName=?, lastName=?, address=?, city=?, state=?, zipCode=?,
                                 telephone=?, startDate=?, hourlyRate=?, SSN=?
            WHERE employeeID=?""";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setString    (1,  e.getFirstName());
            ps.setString    (2,  e.getLastName());
            ps.setString    (3,  e.getAddress());
            ps.setString    (4,  e.getLocation().getCity());
            ps.setString    (5,  e.getLocation().getState());
            ps.setLong    (6,  e.getLocation().getZipCode());
            ps.setString    (7,  e.getTelephone());
            ps.setDate      (8,  new java.sql.Date(e.getStartDate().getTime()));
            ps.setBigDecimal(9,  e.getHourlyRate());
            ps.setString    (10, e.getSsn());
            ps.setLong      (11, e.getEmployeeID());
            return ps.executeUpdate();
        }
    }

    public int delete(long id) throws SQLException {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement("DELETE FROM employee WHERE employeeID=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    public Employee find(long id) throws SQLException {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM employee WHERE employeeID=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Employee> all() throws SQLException {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM employee");
             ResultSet rs = ps.executeQuery()) {

            List<Employee> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    private Employee map(ResultSet r) throws SQLException {
        Employee e = new Employee();
        Location  l = new Location();

        e.setEmployeeID(r.getLong("employeeID"));
        e.setFirstName (r.getString("firstName"));
        e.setLastName  (r.getString("lastName"));
        e.setAddress   (r.getString("address"));

        l.setCity   (r.getString("city"));
        l.setState  (r.getString("state"));
        l.setZipCode(r.getString("zipCode"));
        e.setLocation(l);

        e.setTelephone (r.getString("telephone"));
        e.setStartDate (r.getDate  ("startDate"));
        e.setHourlyRate(r.getBigDecimal("hourlyRate"));
        e.setSsn       (r.getString("SSN"));
        return e;
    }
}
