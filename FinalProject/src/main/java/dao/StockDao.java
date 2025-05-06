package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Stock;

public class StockDao {

	private static final String URL = "jdbc:mysql://localhost:3306/your_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
	
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
    	    "SELECT name, symbol, price, num_shares, type " +
    	    "FROM stock " +
    	    "WHERE num_shares > 0";
    	    rs = st.executeQuery(query);
    	    
    	    while(rs.next()) {
    	    	Stock s = new Stock();
    	    	s.setName(rs.getString("name"));
    	    	s.setSymbol(rs.getString("symbol"));
    	    	s.setPrice(rs.getDouble("price"));
    	    	s.setNumShares(rs.getInt("num_shares"));
    	    	s.setType(rs.getString("type"));
    	    	stocks.add(s);
    	    }
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return stocks;
    }

	public List<Stock> getAllStocks() {
		
		/*
		 * The students code to fetch data from the database will be written here
		 * Return list of stocks
		 */
		List<Stock> stocks = new ArrayList<Stock>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st  = con.createStatement();
            String query = 
              "SELECT name, symbol, price, num_shares, type " +
              "FROM stock";
            rs = st.executeQuery(query);

            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
	}

    public Stock getStockBySymbol(String stockSymbol)
    {
        /*
		 * The students code to fetch data from the database will be written here
		 * Return stock matching symbol
		 */
    	Connection con = null;
    	Statement st = null;
    	ResultSet rs = null;
    	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery("SELECT name,symbol,price,num_shares,type FROM stock WHERE symbol='" + stockSymbol + "'");
            if (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
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
            int rows = st.executeUpdate("UPDATE stock SET price=" + stockPrice + " WHERE symbol='" + stockSymbol + "'");
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
	            "SELECT s.name,s.symbol,s.price,SUM(o.num_shares) AS num_shares,s.type " +
	            "FROM stock s JOIN orders o ON s.symbol=o.stock_symbol " +
	            "GROUP BY s.symbol,s.name,s.price,s.type " +
	            "ORDER BY SUM(o.num_shares) DESC"
	        );
	        while (rs.next()) {
	            Stock s = new Stock();
	            s.setName(rs.getString("name"));
	            s.setSymbol(rs.getString("symbol"));
	            s.setPrice(rs.getDouble("price"));
	            s.setNumShares(rs.getInt("num_shares"));
	            s.setType(rs.getString("type"));
	            stocks.add(s);
	        }
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    return stocks;
	}

    public List<Stock> getCustomerBestsellers(String customerID) {

		/*
		 * The students code to fetch data from the database will be written here.
		 * Get list of customer bestseller stocks
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
                "SELECT s.name,s.symbol,s.price,SUM(o.num_shares) AS num_shares,s.type " +
                "FROM stock s JOIN orders o ON s.symbol=o.stock_symbol " +
                "WHERE o.customer_account_number='" + customerID + "' " +
                "GROUP BY s.symbol,s.name,s.price,s.type " +
                "ORDER BY SUM(o.num_shares) DESC"
            );
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }

	public List getStocksByCustomer(String customerId) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Get stockHoldings of customer with customerId
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
	            "SELECT s.name,s.symbol,s.price,p.num_shares,s.type " +
	            "FROM stock s JOIN portfolio p " +
	            "ON s.symbol=p.stock_symbol " +
	            "WHERE p.customer_account_number='" + customerId + "'"
	        );
	        while (rs.next()) {
	            Stock s = new Stock();
	            s.setName(rs.getString("name"));
	            s.setSymbol(rs.getString("symbol"));
	            s.setPrice(rs.getDouble("price"));
	            s.setNumShares(rs.getInt("num_shares"));
	            s.setType(rs.getString("type"));
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
            rs = st.executeQuery("SELECT name,symbol,price,num_shares,type FROM stock WHERE name LIKE '%" + name + "%'");
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }

    public List<Stock> getStockSuggestions(String customerID) {

		/*
		 * The students code to fetch data from the database will be written here
		 * Return stock suggestions for given "customerId"
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
                "SELECT s.name,s.symbol,s.price,SUM(o.num_shares) AS num_shares,s.type " +
                "FROM stock s JOIN orders o ON s.symbol=o.stock_symbol " +
                "WHERE s.symbol NOT IN (" +
                  "SELECT stock_symbol FROM portfolio " +
                  "WHERE customer_account_number='" + customerID + "'" +
                ") " +
                "GROUP BY s.symbol,s.name,s.price,s.type " +
                "ORDER BY SUM(o.num_shares) DESC LIMIT 5"
            );
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
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
                "SELECT StockName,StockSymbol,SharePrice,NumberOfShares,StockType " +
                "FROM Stock WHERE StockSymbol='" + stockSymbol + "'"
            );
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("StockName"));
                s.setSymbol(rs.getString("StockSymbol"));
                s.setPrice(rs.getDouble("SharePrice"));
                s.setNumShares(rs.getInt("NumberOfShares"));
                s.setType(rs.getString("StockType"));
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
            rs = st.executeQuery("SELECT DISTINCT type FROM stock");
            while (rs.next()) {
                types.add(rs.getString("type"));
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
            rs = st.executeQuery("SELECT name,symbol,price,num_shares,type FROM stock WHERE type='" + stockType + "'");
            while (rs.next()) {
                Stock s = new Stock();
                s.setName(rs.getString("name"));
                s.setSymbol(rs.getString("symbol"));
                s.setPrice(rs.getDouble("price"));
                s.setNumShares(rs.getInt("num_shares"));
                s.setType(rs.getString("type"));
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stocks;
    }
}
