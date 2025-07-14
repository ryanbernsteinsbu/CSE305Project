package com.stocktrader.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.stocktrader.model.Employee;
import com.stocktrader.model.Location;

public class EmployeeDao {

    private static final String URL  = "jdbc:mysql://localhost:3306/cse305?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private static final String USER = "stockuser";
    private static final String PASSWORD = "stockpass";

    public String addEmployee(Employee e) {
        String q = "INSERT INTO employee " +
                "(employeeID, firstName, lastName, address, city, state, zipCode, " +
                "telephone, startDate, hourlyRate, SSN) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setLong(1, e.getEmployeeID());
            ps.setString(2, e.getFirstName());
            ps.setString(3, e.getLastName());
            ps.setString(4, e.getAddress());
            ps.setString(5, e.getLocation().getCity());
            ps.setString(6, e.getLocation().getState());
            ps.setLong(7, e.getLocation().getZipCodeInt());
            ps.setString(8, e.getTelephone());
            ps.setDate(9, e.getStartDate());
            ps.setBigDecimal(10, e.getHourlyRate().setScale(2, RoundingMode.HALF_UP));
            ps.setString(11, e.getSsn());
            return (ps.executeUpdate() > 0) ? "success" : "failure";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }

    public String editEmployee(Employee e) {
        String q = "UPDATE employee SET firstName=?, lastName=?, address=?, city=?, state=?, zipCode=?, " +
                   "telephone=?, startDate=?, hourlyRate=?, SSN=? " +
                   "WHERE employeeID=?";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getLastName());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getLocation().getCity());
            ps.setString(5, e.getLocation().getState());
            ps.setLong(6, e.getLocation().getZipCodeInt());
            ps.setString(7, e.getTelephone());
            ps.setDate(8, e.getStartDate());
            ps.setBigDecimal(9, e.getHourlyRate().setScale(2, RoundingMode.HALF_UP));
            ps.setString(10, e.getSsn());
            ps.setLong(11, e.getEmployeeID());
            return (ps.executeUpdate() > 0) ? "success" : "failure";
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }

    public String deleteEmployee(String id) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement("DELETE FROM employee WHERE employeeID=?")) {

            ps.setLong(1, Long.parseLong(id));
            return (ps.executeUpdate() > 0) ? "success" : "failure";

        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            return "failure";
        }
    }


    public Employee getEmployee(long id) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM employee WHERE employeeID=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapEmployee(rs) : null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public Employee getEmployee(String id) {
        try {
            return getEmployee(Long.parseLong(id));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Employee> getEmployees() {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM employee");
             ResultSet rs = ps.executeQuery()) {

            List<Employee> list = new ArrayList<>();
            while (rs.next()) list.add(mapEmployee(rs));
            return list;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ArrayList<>();  // Return an empty list in case of an error
        }
    }

    private Employee mapEmployee(ResultSet r) {
        try {
            Employee e = new Employee();
            Location l = new Location();

            e.setEmployeeID(r.getLong("employeeID"));
            e.setFirstName(r.getString("firstName"));
            e.setLastName(r.getString("lastName"));
            e.setAddress(r.getString("address"));

            l.setCity(r.getString("city"));
            l.setState(r.getString("state"));
            l.setZipCode(Integer.parseInt(r.getString("zipCode")));
            e.setLocation(l);

            e.setTelephone(r.getString("telephone"));
            e.setStartDate(r.getDate("startDate"));
            e.setHourlyRate(r.getBigDecimal("hourlyRate").setScale(2, RoundingMode.HALF_UP));
            e.setSsn(r.getString("SSN"));
            return e;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public Employee getHighestRevenueEmployee() {
        String q = "SELECT e.* FROM employee e JOIN ("
                 + "SELECT o.employeeID, SUM(o.numShares * o.pricePerShare) AS revenue "
                 + "FROM orders o WHERE o.employeeID IS NOT NULL "
                 + "GROUP BY o.employeeID ORDER BY revenue DESC LIMIT 1"
                 + ") topEmp ON e.employeeID = topEmp.employeeID";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? mapEmployee(rs) : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getEmployeeID(String username) {
        String q = "SELECT employeeID FROM employee WHERE username = ?";
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(q)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                     ? String.valueOf(rs.getLong("employeeID"))
                     : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
