package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import model.RevenueItem;

public class SalesDao {
	
	private static final String URL = "jdbc:mysql://localhost:3306/demo";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
	
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
            "SELECT o.DateTime AS date, o.NumberOfShares AS numShares, o.AccountNumber AS accountId, " +
            "s.SharePrice AS pricePerShare, o.StockSymbol AS stockSymbol " +
            "FROM Orders o JOIN Stock s ON o.StockSymbol = s.StockSymbol " +
            "WHERE MONTH(o.DateTime) = " + month + " AND YEAR(o.DateTime) = " + year
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
            "SELECT o.DateTime AS date, o.NumberOfShares AS numShares, o.AccountNumber AS accountId, " +
            "s.SharePrice AS pricePerShare, o.StockSymbol AS stockSymbol " +
            "FROM Orders o " +
            "JOIN Stock s ON o.StockSymbol = s.StockSymbol " +
            "JOIN Account a ON o.AccountNumber = a.AccountNumber " +
            "JOIN Customers c ON a.CustomerID = c.CustomerID " +
            "WHERE o.StockSymbol = '" + searchKeyword + "' " +
            "OR s.StockType = '" + searchKeyword + "' " +
            "OR c.LastName = '" + searchKeyword + "'"
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
}
