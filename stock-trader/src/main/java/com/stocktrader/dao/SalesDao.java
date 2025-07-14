package com.stocktrader.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import com.stocktrader.model.RevenueItem;

public class SalesDao {
	
    private static final String URL  = "jdbc:mysql://localhost:3306/cse305?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private static final String USER = "stockuser";
    private static final String PASSWORD = "stockpass";
    
    private List<RevenueItem> getDummyRevenueItems()
    {
        List<RevenueItem> items = new ArrayList<RevenueItem>();

		/*Sample data begins*/
        for (int i = 0; i < 10; i++) {
            RevenueItem item = new RevenueItem();
            item.setDate(new Date());
            item.setNumShares(5);
            item.setAccountId("foo");
            item.setPricePerShare(50.0);
            item.setStockSymbol("AAPL");
            item.setAmount(150.0);
            items.add(item);
        }
        /*Sample data ends*/

        return items;
    }
    
    public List<RevenueItem> getSalesReport(String month, String year) {
    List<RevenueItem> items = new ArrayList<>();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st = con.createStatement();
        rs = st.executeQuery(
            "SELECT o.dateTime AS date, o.numShares AS numShares, o.accountNum AS accountNumber, " +
            "s.sharePrice AS pricePerShare, o.stockSymbol AS stockSymbol " +
            "FROM orders o JOIN stock s ON o.stockSymbol = s.stockSymbol " +
            "WHERE MONTH(o.dateTime) = " + month + " AND YEAR(o.dateTime) = " + year
        );
        while (rs.next()) {
            RevenueItem item = new RevenueItem();
            item.setDate(rs.getDate("date"));
            item.setNumShares(rs.getInt("numShares"));
            item.setAccountId(rs.getString("accountId"));
            item.setPricePerShare(rs.getDouble("pricePerShare"));
            item.setStockSymbol(rs.getString("stockSymbol"));
            item.setAmount(item.getNumShares() * item.getPricePerShare());
            items.add(item);
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return items;
}

public List<RevenueItem> getSummaryListing(String searchKeyword) {
    List<RevenueItem> items = new ArrayList<>();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st = con.createStatement();
        rs = st.executeQuery(
            "SELECT o.dateTime AS date, o.numShares AS numShares, o.accountNum AS accountId, " +
            "s.sharePrice AS pricePerShare, o.stockSymbol AS stockSymbol " +
            "FROM orders o " +
            "JOIN stock s ON o.stockSymbol = s.stockSymbol " +
            "JOIN accounts a ON o.accountNum = a.accountNum " +
            "JOIN customers c ON a.customerID = c.customerID " +
            "WHERE o.stockSymbol = '" + searchKeyword + "' " +
            "OR s.stockType = '" + searchKeyword + "' " +
            "OR c.lastName = '" + searchKeyword + "'"
        );
        while (rs.next()) {
            RevenueItem item = new RevenueItem();
            item.setDate(rs.getDate("date"));
            item.setNumShares(rs.getInt("numShares"));
            item.setAccountId(rs.getString("accountID"));
            item.setPricePerShare(rs.getDouble("pricePerShare"));
            item.setStockSymbol(rs.getString("stockSymbol"));
            item.setAmount(item.getNumShares() * item.getPricePerShare());
            items.add(item);
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return items;
}
}
