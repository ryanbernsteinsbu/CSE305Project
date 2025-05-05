package dao;

import model.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDao {
	
	private static final String URL = "jdbc:mysql://localhost:3306/your_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";
    
    public Order getDummyTrailingStopOrder() {
        TrailingStopOrder order = new TrailingStopOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPercentage(12.0);
        return order;
    }

    public Order getDummyMarketOrder() {
        MarketOrder order = new MarketOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyMarketOnCloseOrder() {
        MarketOnCloseOrder order = new MarketOnCloseOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setBuySellType("buy");
        return order;
    }

    public Order getDummyHiddenStopOrder() {
        HiddenStopOrder order = new HiddenStopOrder();

        order.setId(1);
        order.setDatetime(new Date());
        order.setNumShares(5);
        order.setPricePerShare(145.0);
        return order;
    }

    public List<Order> getDummyOrders() {
        List<Order> orders = new ArrayList<Order>();

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyTrailingStopOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyMarketOnCloseOrder());
        }

        for (int i = 0; i < 3; i++) {
            orders.add(getDummyHiddenStopOrder());
        }

        return orders;
    }

    public String submitOrder(Order order, Customer customer, Employee employee, Stock stock) {

		/*
		 * Student code to place stock order
		 * Employee can be null, when the order is placed directly by Customer
         * */

    	Connection con = null;
        Statement st = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            String orderType;
            if (order instanceof MarketOrder) orderType = ((MarketOrder)order).getBuySellType();
            else if (order instanceof MarketOnCloseOrder) orderType = ((MarketOnCloseOrder)order).getBuySellType();
            else orderType = "sell";
            String priceType;
            if (order instanceof TrailingStopOrder) priceType = "trailing stop " + ((TrailingStopOrder)order).getPercentage() + "%";
            else if (order instanceof HiddenStopOrder) priceType = "$" + ((HiddenStopOrder)order).getPricePerShare();
            else if (order instanceof MarketOnCloseOrder) priceType = "market on close";
            else priceType = "market";
            double transactionFee = stock.getPrice() * order.getNumShares() * 0.05;
            Timestamp ts = new Timestamp(order.getDatetime().getTime());
            String sql = "INSERT INTO StockOrder (OrderID,AccountNumber,EmployeeID,StockSymbol,OrderType,NumberOfShares,DateTime,TransactionFee,PriceType) VALUES ("
                + order.getId() + "," 
                + customer.getAccountNumber() + "," 
                + (employee != null ? employee.getEmployeeID() : "NULL") + ",'" 
                + stock.getSymbol() + "','" 
                + orderType + "'," 
                + order.getNumShares() + ",'" 
                + ts.toString() + "'," 
                + transactionFee + ",'" 
                + priceType + "')";
            int r = st.executeUpdate(sql);
            return r > 0 ? "success" : "failure";
        } catch (Exception e) {
            System.out.println(e);
            return "failure";
        }

    }

    public List<Order> getOrderByStockSymbol(String stockSymbol) {
        /*
		 * Student code to get orders by stock symbol
         */
    	List<Order> orders = new ArrayList<Order>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT OrderID,AccountNumber,EmployeeID,StockSymbol,OrderType,NumberOfShares,DateTime,TransactionFee,PriceType FROM StockOrder WHERE StockSymbol='"
                + stockSymbol + "'"
            );
            while (rs.next()) {
                String pt = rs.getString("PriceType");
                Order o;
                if (pt.startsWith("trailing")) {
                    TrailingStopOrder t = new TrailingStopOrder();
                    t.setPercentage(Double.parseDouble(pt.replaceAll("[^0-9.]", "")));
                    o = t;
                } else if (pt.startsWith("$")) {
                    HiddenStopOrder h = new HiddenStopOrder();
                    h.setPricePerShare(Double.parseDouble(pt.substring(1)));
                    o = h;
                } else if (pt.contains("close")) {
                    MarketOnCloseOrder m = new MarketOnCloseOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                } else {
                    MarketOrder m = new MarketOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                }
                o.setId(rs.getInt("OrderID"));
                o.setDatetime(rs.getTimestamp("DateTime"));
                o.setNumShares(rs.getInt("NumberOfShares"));
                orders.add(o);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return orders;
    }

    public List<Order> getOrderByCustomerName(String customerName) {
         /*
		 * Student code to get orders by customer name
         */
    	List<Order> orders = new ArrayList<Order>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT so.OrderID,so.AccountNumber,so.EmployeeID,so.StockSymbol,so.OrderType,so.NumberOfShares,so.DateTime,so.TransactionFee,so.PriceType "
              + "FROM StockOrder so JOIN Account a ON so.AccountNumber=a.AccountNumber "
              + "JOIN Customers c ON a.CustomerID=c.CustomerID "
              + "WHERE c.LastName='" + customerName + "'"
            );
            while (rs.next()) {
                String pt = rs.getString("PriceType");
                Order o;
                if (pt.startsWith("trailing")) {
                    TrailingStopOrder t = new TrailingStopOrder();
                    t.setPercentage(Double.parseDouble(pt.replaceAll("[^0-9.]", "")));
                    o = t;
                } else if (pt.startsWith("$")) {
                    HiddenStopOrder h = new HiddenStopOrder();
                    h.setPricePerShare(Double.parseDouble(pt.substring(1)));
                    o = h;
                } else if (pt.contains("close")) {
                    MarketOnCloseOrder m = new MarketOnCloseOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                } else {
                    MarketOrder m = new MarketOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                }
                o.setId(rs.getInt("OrderID"));
                o.setDatetime(rs.getTimestamp("DateTime"));
                o.setNumShares(rs.getInt("NumberOfShares"));
                orders.add(o);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return orders;
    }

    public List<Order> getOrderHistory(String customerId) {
        /*
		 * The students code to fetch data from the database will be written here
		 * Show orders for given customerId
		 */
    	List<Order> orders = new ArrayList<Order>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT so.OrderID,so.AccountNumber,so.EmployeeID,so.StockSymbol,so.OrderType,so.NumberOfShares,so.DateTime,so.TransactionFee,so.PriceType "
              + "FROM StockOrder so JOIN Account a ON so.AccountNumber=a.AccountNumber "
              + "WHERE a.CustomerID='" + customerId + "'"
            );
            while (rs.next()) {
                String pt = rs.getString("PriceType");
                Order o;
                if (pt.startsWith("trailing")) {
                    TrailingStopOrder t = new TrailingStopOrder();
                    t.setPercentage(Double.parseDouble(pt.replaceAll("[^0-9.]", "")));
                    o = t;
                } else if (pt.startsWith("$")) {
                    HiddenStopOrder h = new HiddenStopOrder();
                    h.setPricePerShare(Double.parseDouble(pt.substring(1)));
                    o = h;
                } else if (pt.contains("close")) {
                    MarketOnCloseOrder m = new MarketOnCloseOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                } else {
                    MarketOrder m = new MarketOrder();
                    m.setBuySellType(rs.getString("OrderType"));
                    o = m;
                }
                o.setId(rs.getInt("OrderID"));
                o.setDatetime(rs.getTimestamp("DateTime"));
                o.setNumShares(rs.getInt("NumberOfShares"));
                orders.add(o);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return orders;
    }


    public List<OrderPriceEntry> getOrderPriceHistory(String orderId) {

        /*
		 * The students code to fetch data from the database will be written here
		 * Query to view price history of hidden stop order or trailing stop order
		 * Use setPrice to show hidden-stop price and trailing-stop price
		 */
        List<OrderPriceEntry> orderPriceHistory = new ArrayList<OrderPriceEntry>();

        for (int i = 0; i < 10; i++) {
            OrderPriceEntry entry = new OrderPriceEntry();
            entry.setOrderId(orderId);
            entry.setDate(new Date());
            entry.setStockSymbol("aapl");
            entry.setPricePerShare(150.0);
            entry.setPrice(100.0);
            orderPriceHistory.add(entry);
        }
        return orderPriceHistory;
    }
}
