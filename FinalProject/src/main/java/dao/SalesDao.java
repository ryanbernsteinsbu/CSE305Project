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
	
	private static final String URL = "jdbc:mysql://localhost:3306/your_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";
	
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

		/*
		 * The students code to fetch data from the database will be written here
		 * Query to get sales report for a particular month and year
		 */

    	List<RevenueItem> items = new ArrayList<RevenueItem>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT so.DateTime AS date, so.NumberOfShares AS numShares, so.AccountNumber AS accountId, s.SharePrice AS pricePerShare, so.StockSymbol AS stockSymbol " +
                "FROM StockOrder so JOIN Stock s ON so.StockSymbol=s.StockSymbol " +
                "WHERE MONTH(so.DateTime)=" + month + " AND YEAR(so.DateTime)=" + year
            );
            while (rs.next()) {
                RevenueItem item = new RevenueItem();
                item.setDate(rs.getDate("date"));
                item.setNumShares(rs.getInt("numShares"));
                item.setAccountId(rs.getString("accountId"));
                item.setPricePerShare(rs.getDouble("pricePerShare"));
                item.setStockSymbol(rs.getString("stockSymbol"));
                item.setAmount(rs.getInt("numShares") * rs.getDouble("pricePerShare"));
                items.add(item);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return items;
    }



    public List<RevenueItem> getSummaryListing(String searchKeyword) {

    	List<RevenueItem> items = new ArrayList<RevenueItem>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT so.DateTime AS date, so.NumberOfShares AS numShares, so.AccountNumber AS accountId, s.SharePrice AS pricePerShare, so.StockSymbol AS stockSymbol " +
                "FROM StockOrder so JOIN Stock s ON so.StockSymbol=s.StockSymbol JOIN Account a ON so.AccountNumber=a.AccountNumber JOIN Customers c ON a.CustomerID=c.CustomerID " +
                "WHERE so.StockSymbol='" + searchKeyword + "' OR s.StockType='" + searchKeyword + "' OR c.LastName='" + searchKeyword + "'"
            );
            while (rs.next()) {
                RevenueItem item = new RevenueItem();
                item.setDate(rs.getDate("date"));
                item.setNumShares(rs.getInt("numShares"));
                item.setAccountId(rs.getString("accountId"));
                item.setPricePerShare(rs.getDouble("pricePerShare"));
                item.setStockSymbol(rs.getString("stockSymbol"));
                item.setAmount(rs.getInt("numShares") * rs.getDouble("pricePerShare"));
                items.add(item);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return items;
    }
}
