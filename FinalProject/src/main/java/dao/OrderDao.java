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
    private static final String PASSWORD = "root";
    
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
    Statement  st  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
	
        // Base fields
        int    id        = order.getId();
        Timestamp ts     = new Timestamp(order.getDatetime().getTime());
        int    shares    = order.getNumShares();
	int    accountNumber = customer.getAccountNumber();
        String employeeID    = (employee != null ? "'" + employee.getEmployeeID() + "'" : "NULL");

        // Determine orderType and subclass‐specific columns
        String orderType    = "unknown";
        String buySellType  = "NULL";
        String hiddenStop   = "NULL";
        String trailPercent = "NULL";

        if (order instanceof MarketOrder) {
            orderType   = "market";
            buySellType = "'" + ((MarketOrder)order).getBuySellType() + "'";
        }
        else if (order instanceof MarketOnCloseOrder) {
            orderType   = "marketOnClose";
            buySellType = "'" + ((MarketOnCloseOrder)order).getBuySellType() + "'";
        }
        else if (order instanceof HiddenStopOrder) {
            orderType  = "hiddenStop";
            hiddenStop = String.valueOf(((HiddenStopOrder)order).getPricePerShare());
        }
        else if (order instanceof TrailingStopOrder) {
            orderType    = "trailingStop";
            trailPercent = String.valueOf(((TrailingStopOrder)order).getPercentage());
        }

        // Build and execute INSERT
        String sql = ""
          + "INSERT INTO orders "
          + "(orderID, dateTime, numShares, orderType, accountNum, employeeID, buySellType, hiddenStop, trailPercent) VALUES ("
          +   id + ", '"
          +   ts.toString() + "', "
          +   shares + ", '"
	  +   orderType + "', "	
	  +   accountNumber + ", '"
	  +   employeeID + ", '"
          +   buySellType + ", "
          +   hiddenStop + ", "
          +   trailPercent
          + ")";
        int rows = st.executeUpdate(sql);
        return rows > 0 ? "success" : "failure";
    } catch (Exception e) {
        System.out.println(e);
        return "failure";
    }
}

    public List<Order> getOrderByStockSymbol(String stockSymbol) {
    List<Order> orders = new ArrayList<>();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st = con.createStatement();
        String sql =
            "SELECT o.orderID, o.datetime, o.numShares, o.orderType, o.accountNumber, o.employeeID, o.buySellType, o.hiddenStop, o.trailPercent " +
            "FROM orders o " +
            "WHERE o.stockSymbol = '" + stockSymbol + "'";
        rs = st.executeQuery(sql);
        while (rs.next()) {
            String type = rs.getString("orderType");
            Order o;
            if ("market".equals(type)) {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("marketOnClose".equals(type)) {
                MarketOnCloseOrder m = new MarketOnCloseOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("hiddenStop".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("hiddenStop"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("trailPercent"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("id"));
            o.setDatetime(rs.getTimestamp("datetime"));
            o.setNumShares(rs.getInt("numShares"));
            orders.add(o);
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return orders;
}

    public List<Order> getOrderByCustomerName(String customerName) {
    List<Order> orders = new ArrayList<>();
    Connection con = null;
    Statement  st  = null;
    ResultSet  rs  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
        String sql =
            "SELECT o.id,o.datetime,o.numShares,o.orderType,o.buySellType,o.hiddenStop,o.trailPercent " +
            "FROM orders o " +
            "JOIN accounts a ON o.accountNumber=a.accountId " +
            "JOIN customers c ON a.customerId=c.customerId " +
            "WHERE c.lastName='" + customerName + "'";
        rs = st.executeQuery(sql);
        while (rs.next()) {
            String type = rs.getString("orderType");
            Order o;
            if ("market".equals(type)) {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("marketOnClose".equals(type)) {
                MarketOnCloseOrder m = new MarketOnCloseOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("hiddenStop".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("hiddenStop"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("trailPercent"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("id"));
            o.setDatetime(rs.getTimestamp("datetime"));
            o.setNumShares(rs.getInt("numShares"));
            orders.add(o);
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return orders;
}

    public List<Order> getOrderHistory(String customerId) {
    List<Order> orders = new ArrayList<>();
    Connection con = null;
    Statement  st  = null;
    ResultSet  rs  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
        String sql =
            "SELECT o.id,o.datetime,o.numShares,o.orderType,o.buySellType,o.hiddenStop,o.trailPercent " +
            "FROM orders o " +
            "JOIN accounts a ON o.accountNumber=a.accountId " +
            "WHERE a.customerId='" + customerId + "'";
        rs = st.executeQuery(sql);
        while (rs.next()) {
            String type = rs.getString("orderType");
            Order o;
            if ("market".equals(type)) {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("marketOnClose".equals(type)) {
                MarketOnCloseOrder m = new MarketOnCloseOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            } else if ("hiddenStop".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("hiddenStop"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("trailPercent"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("id"));
            o.setDatetime(rs.getTimestamp("datetime"));
            o.setNumShares(rs.getInt("numShares"));
            orders.add(o);
        }
    } catch (Exception e) {
        System.out.println(e);
    }
    return orders;
}
	
    public List<OrderPriceEntry> getOrderPriceHistory(String orderId) {
    List<OrderPriceEntry> history = new ArrayList<>();
    Connection con = null;
    Statement  st  = null;
    ResultSet  rs  = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        st  = con.createStatement();
        String sql =
            "SELECT o.id, o.stockSymbol, o.datetime, o.orderType, o.hiddenStop, o.trailPercent, s.sharePrice " +
            "FROM orders o JOIN stock s ON o.stockSymbol = s.StockSymbol " +
            "WHERE o.id = " + orderId;
        rs = st.executeQuery(sql);
        while (rs.next()) {
            OrderPriceEntry e = new OrderPriceEntry();
            e.setOrderId       ( rs.getString("id") );
            e.setDate          ( rs.getTimestamp("datetime") );
            e.setStockSymbol   ( rs.getString("stockSymbol") );
            double sharePrice  = rs.getDouble("sharePrice");
            e.setPricePerShare ( sharePrice );
            String type        = rs.getString("orderType");
            if ("hiddenStop".equals(type)) {
                e.setPrice(rs.getDouble("hiddenStop"));
            }
            else if ("trailingStop".equals(type)) {
                double pct = rs.getDouble("trailPercent");
                e.setPrice(sharePrice * (1 - pct/100.0));
            }
            else {
                e.setPrice(sharePrice);
            }
            history.add(e);
        }
    } catch (Exception ex) {
        System.out.println(ex);
    }
    return history;
}
}
