package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Stock;

public class StockDao {

	private static final String URL = "jdbc:mysql://localhost:3306/cse305?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
	
    public Stock getDummyStock() {
        Stock stock = new Stock();
        stock.setName("Apple");
        stock.setSymbol("AAPL");
        stock.setPrice(150.0);
        stock.setNumShares(1200);
        stock.setType("Technology");

        return stock;
    }

    public List<Stock> getDummyStocks() {
        List<Stock> stocks = new ArrayList<Stock>();

		/*Sample data begins*/
        for (int i = 0; i < 10; i++) {
            stocks.add(getDummyStock());
        }
		/*Sample data ends*/

        return stocks;
    }

    public List<Stock> getActivelyTradedStocks() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Query to fetch details of all the stocks has to be implemented
		 * Return list of actively traded stocks
		 */

    	List<Stock> stocks = new ArrayList<Stock>();
    	Connection con = null;
    	Statement st = null;
    	ResultSet rs = null;
    	
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		con = DriverManager.getConnection(URL, USER, PASSWORD);
    		st = con.createStatement();
    		String query = 
    	    "SELECT stockName, stockSymbol, sharePrice, numShares, stockType " +
    	    "FROM stock " +
    	    "WHERE numShares > 0";
    	    rs = st.executeQuery(query);
    	    
    	    while(rs.next()) {
    	    	Stock s = new Stock();
    	    	s.setName(rs.getString("stockName"));
                s.setSymbol(rs.getString("stockSymbol"));
                s.setPrice(rs.getDouble("sharePrice"));
                s.setNumShares(rs.getInt("numShares"));
                s.setType(rs.getString("stockType"));
    	    	stocks.add(s);
    	    }
    	} catch (Exception e) {
    		System.out.println(e);
    		return null;
    	}
    	return stocks;
    }

public List<Stock> getAllStocks() {
    List<Stock> stocks = new ArrayList<>();
    Connection con = null;
    Statement  st  = null;
    ResultSet  rs  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
        String query =
            "SELECT stockName, stockSymbol, sharePrice, numShares, stockType " +
            "FROM stock";
        rs = st.executeQuery(query);
        while (rs.next()) {
            Stock s = new Stock();
            s.setName     (rs.getString("stockName"));
            s.setSymbol   (rs.getString("stockSymbol"));
            s.setPrice    (rs.getDouble("sharePrice"));
            s.setNumShares(rs.getInt   ("numShares"));
            s.setType     (rs.getString("stockType"));
            stocks.add(s);
        }
    } catch (Exception e) {
        System.out.println(e);
        return null;
    }
    return stocks;
}

public Stock getStockBySymbol(String stockSymbol) {
    Connection con = null;
    Statement  st  = null;
    ResultSet  rs  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
        rs = st.executeQuery(
            "SELECT stockName, stockSymbol, sharePrice, numShares, stockType " +
            "FROM stock " +
            "WHERE stockSymbol = '" + stockSymbol + "'"
        );
        if (rs.next()) {
            Stock s = new Stock();
            s.setName     (rs.getString("stockName"));
            s.setSymbol   (rs.getString("stockSymbol"));
            s.setPrice    (rs.getDouble("sharePrice"));
            s.setNumShares(rs.getInt   ("numShares"));
            s.setType     (rs.getString("stockType"));
            return s;
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return null;
}


    public String setStockPrice(String stockSymbol, double stockPrice) {
        /*
         * The students code to fetch data from the database will be written here
         * Perform price update of the stock symbol
         */
    	Connection con = null;
        Statement st = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            int rows = st.executeUpdate("UPDATE stock SET sharePrice=" + stockPrice + " WHERE stockSymbol='" + stockSymbol + "'");
            return rows > 0 ? "success" : "failure";
        } catch (Exception e) {
            System.out.println(e);
            return "failure";
        }
    }
	
	public List<Stock> getOverallBestsellers() {

		/*
		 * The students code to fetch data from the database will be written here
		 * Get list of bestseller stocks
		 */
	    List<Stock> stocks = new ArrayList<Stock>();
	    Connection con = null;
	    Statement st = null;
	    ResultSet rs = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        con = DriverManager.getConnection(URL, USER, PASSWORD);
	        st = con.createStatement();
	        rs = st.executeQuery(
	            "SELECT s.stockName, s.stockSymbol, s.sharePrice, SUM(o.numShares) AS numShares, s.stockType " +
	            "FROM stock s JOIN orders o ON s.stockSymbol = o.stockSymbol " +
	            "GROUP BY s.stockSymbol, s.stockName, s.sharePrice, s.stockType " +
	            "ORDER BY SUM(o.numShares) DESC"
	        );
	        while (rs.next()) {
	            Stock s = new Stock();
	            s.setName(rs.getString("stockName"));
	            s.setSymbol(rs.getString("stockSymbol"));
	            s.setPrice(rs.getDouble("sharePrice"));
	            s.setNumShares(rs.getInt("numShares"));
	            s.setType(rs.getString("stockType"));
	            stocks.add(s);
	        }
	    } catch (Exception e) {
	        System.out.println(e);
	        return null;
	    }
	    return stocks;
	}

	public List<Stock> getCustomerBestsellers(String customerID) {
	    List<Stock> stocks = new ArrayList<>();
	    Connection con = null;
	    Statement  st  = null;
	    ResultSet  rs  = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        con = DriverManager.getConnection(URL, USER, PASSWORD);
	        st  = con.createStatement();

	        String query =
	            "SELECT s.stockName, s.stockSymbol, s.sharePrice, SUM(o.numShares) AS numShares, s.stockType " +
	            "FROM stock s " +
	            "JOIN orders o    ON s.stockSymbol = o.stockSymbol " +
	            "JOIN accounts a  ON o.accountNum   = a.accountNum " +
	            "WHERE a.customerID = '" + customerID + "' " +
	            "GROUP BY s.stockSymbol, s.stockName, s.sharePrice, s.stockType " +
	            "ORDER BY SUM(o.numShares) DESC";
	        rs = st.executeQuery(query);

	        while (rs.next()) {
	            Stock s = new Stock();
	            s.setName     (rs.getString("stockName"));
	            s.setSymbol   (rs.getString("stockSymbol"));
	            s.setPrice    (rs.getDouble("sharePrice"));
	            s.setNumShares(rs.getInt   ("numShares"));
	            s.setType     (rs.getString("stockType"));
	            stocks.add(s);
	        }
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    return stocks;
	}

	public List<Stock> getStocksByCustomer(String customerId) {
	    List<Stock> stocks = new ArrayList<>();
	    Connection con = null;
	    Statement  st  = null;
	    ResultSet  rs  = null;
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        con = DriverManager.getConnection(URL, USER, PASSWORD);
	        st  = con.createStatement();

	        String query =
	            "SELECT s.stockName, s.stockSymbol, s.sharePrice, SUM(o.numShares) AS numShares, s.stockType " +
	            "FROM stock s " +
	            "JOIN orders o    ON s.stockSymbol = o.stockSymbol " +
	            "JOIN accounts a  ON o.accountNum   = a.accountNum " +
	            "WHERE a.customerID = '" + customerId + "' " +
	            "GROUP BY s.stockSymbol, s.stockName, s.sharePrice, s.stockType";
	        rs = st.executeQuery(query);

	        while (rs.next()) {
	            Stock s = new Stock();
	            s.setName     (rs.getString("stockName"));
	            s.setSymbol   (rs.getString("stockSymbol"));
	            s.setPrice    (rs.getDouble("sharePrice"));
	            s.setNumShares(rs.getInt   ("numShares"));
	            s.setType     (rs.getString("stockType"));
	            stocks.add(s);
	        }
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    return stocks;
	}

    public List<Stock> getStocksByName(String name) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of stocks matching "name"
		 */
    	List<Stock> stocks = new ArrayList<Stock>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery("SELECT stockName,stockSymbol,sharePrice,numShares,stockType FROM stock WHERE stockName LIKE '%" + name + "%'");
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("stockName"));
                s.setSymbol(rs.getString("stockSymbol"));
                s.setPrice(rs.getDouble("sharePrice"));
                s.setNumShares(rs.getInt("numShares"));
                s.setType(rs.getString("stockType"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }

    public List<Stock> getStockSuggestions(String customerID) {
        List<Stock> stocks = new ArrayList<>();
        Connection con = null;
        Statement  st  = null;
        ResultSet  rs  = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st  = con.createStatement();

            String query =
                "SELECT s.stockName, s.stockSymbol, s.sharePrice, SUM(o.numShares) AS numShares, s.stockType " +
                "FROM stock s " +
                "JOIN orders o    ON s.stockSymbol = o.stockSymbol " +
                "WHERE s.stockSymbol NOT IN ( " +
                "    SELECT o2.stockSymbol " +
                "    FROM orders o2 " +
                "    JOIN accounts a2 ON o2.accountNum = a2.accountNum " +
                "    WHERE a2.customerID = '" + customerID + "' " +
                ") " +
                "GROUP BY s.stockSymbol, s.stockName, s.sharePrice, s.stockType " +
                "ORDER BY SUM(o.numShares) DESC " +
                "LIMIT 5";
            rs = st.executeQuery(query);

            while (rs.next()) {
                Stock s = new Stock();
                s.setName     (rs.getString("stockName"));
                s.setSymbol   (rs.getString("stockSymbol"));
                s.setPrice    (rs.getDouble("sharePrice"));
                s.setNumShares(rs.getInt   ("numShares"));
                s.setType     (rs.getString("stockType"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }

    public List<Stock> getStockPriceHistory(String stockSymbol) {

		/*
		 * The students code to fetch data from the database
		 * Return list of stock objects, showing price history
		 */
    	List<Stock> stocks = new ArrayList<Stock>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT stockName,stockSymbol,sharePrice,numShares,stockType " +
                "FROM stock WHERE stockSymbol='" + stockSymbol + "'"
            );
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("stockName"));
                s.setSymbol(rs.getString("stockSymbol"));
                s.setPrice(rs.getDouble("sharePrice"));
                s.setNumShares(rs.getInt("numShares"));
                s.setType(rs.getString("stockType"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }

    public List<String> getStockTypes() {

		/*
		 * The students code to fetch data from the database will be written here.
		 * Populate types with stock types
		 */
    	List<String> types = new ArrayList<String>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery("SELECT DISTINCT stockType FROM stock");
            while (rs.next()) {
                types.add(rs.getString("stockType"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return types;
    }

    public List<Stock> getStockByType(String stockType) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of stocks of type "stockType"
		 */
    	List<Stock> stocks = new ArrayList<Stock>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery("SELECT stockName,stockSymbol,sharePrice,numShares,stockType FROM stock WHERE stockType='" + stockType + "'");
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("stockName"));
                s.setSymbol(rs.getString("stockSymbol"));
                s.setPrice(rs.getDouble("sharePrice"));
                s.setNumShares(rs.getInt("numShares"));
                s.setType(rs.getString("stockType"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }
}
