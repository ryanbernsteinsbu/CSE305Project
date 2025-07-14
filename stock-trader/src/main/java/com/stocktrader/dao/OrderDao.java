package com.stocktrader.dao;

import com.stocktrader.model.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDao {
	
	private static final String URL = "jdbc:mysql://localhost:3306/cse305?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/New_York";
    private static final String USER = "stockuser";
    private static final String PASSWORD = "stockpass";
    
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

public String submitOrder(Order order,
                          Customer customer,
                          Employee employee,
                          Stock stock) {
    Connection con = null;
    PreparedStatement psCheck, psInsert, psUpdateStock = null;
    ResultSet rs = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(URL, USER, PASSWORD);
        con.setAutoCommit(false);

        int shares          = order.getNumShares();
        int accountNumber   = customer.getAccountNumber();
        String symbol       = stock.getSymbol();
        String buySellType  = (order instanceof MarketOrder)
                              ? ((MarketOrder)order).getBuySellType()
                              : order instanceof MarketOnCloseOrder
                                ? ((MarketOnCloseOrder)order).getBuySellType()
                                : order instanceof HiddenStopOrder
                                  ? "sell"  // or derive appropriately
                                  : "sell";

        // 1) Check available shares for BUY
        if ("buy".equalsIgnoreCase(buySellType)) {
            psCheck = con.prepareStatement(
              "SELECT numShares FROM stock WHERE stockSymbol = ?");
            psCheck.setString(1, symbol);
            rs = psCheck.executeQuery();
            if (!rs.next() || rs.getInt("numShares") < shares) {
                con.rollback();
                return "failure";
            }
            rs.close();
            psCheck.close();
        }
        // 2) Check customer holdings for SELL
        else if ("sell".equalsIgnoreCase(buySellType)) {
            psCheck = con.prepareStatement(
              "SELECT " +
              "  COALESCE(SUM(CASE WHEN buySellType='buy'  THEN numShares END),0) - " +
              "  COALESCE(SUM(CASE WHEN buySellType='sell' THEN numShares END),0) " +
              "  AS holding " +
              "FROM orders " +
              "WHERE accountNum=? AND stockSymbol=?");
            psCheck.setInt(1, accountNumber);
            psCheck.setString(2, symbol);
            rs = psCheck.executeQuery();
            rs.next();
            if (rs.getInt("holding") < shares) {
                con.rollback();
                return "failure";
            }
            rs.close();
            psCheck.close();
        }
        
        String orderType = null;
        String pricePerShare = null;
        String percentage = null;
        if (order instanceof MarketOrder) {
            orderType   = "market";
            buySellType = "'" + ((MarketOrder)order).getBuySellType() + "'";
        }
        else if (order instanceof MarketOnCloseOrder) {
            orderType   = "marketOnClose";
            buySellType = "'" + ((MarketOnCloseOrder)order).getBuySellType() + "'";
        }
        else if (order instanceof HiddenStopOrder) {
            orderType  = "pricePerShare";
            pricePerShare = String.valueOf(((HiddenStopOrder)order).getPricePerShare());
        }
        else if (order instanceof TrailingStopOrder) {
            orderType    = "trailingStop";
            percentage = String.valueOf(((TrailingStopOrder)order).getPercentage());
        }
        // 3) Insert the order with auto-generated ID
        String sqlInsert = "INSERT INTO orders "
          + "(stockSymbol, dateTime, numShares,"  
          + " orderType, accountNum, employeeID,"       
          + " buySellType, pricePerShare, percentage) "  
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        psInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
        psInsert.setString(1, symbol);
        psInsert.setTimestamp(2,
             new Timestamp(order.getDatetime().getTime()));
        psInsert.setInt(3, shares);
        psInsert.setString(4, orderType);
        psInsert.setInt(5, accountNumber);
        if (employee != null)
            psInsert.setLong(6, employee.getEmployeeID());
        else
            psInsert.setNull(6, java.sql.Types.BIGINT);
        psInsert.setString(7, buySellType);
        psInsert.setString(8, pricePerShare);
        psInsert.setString(9, percentage);

        int rows = psInsert.executeUpdate();
        if (rows != 1) {
            con.rollback();
            return "failure";
        }
        // retrieve generated orderID
        rs = psInsert.getGeneratedKeys();
        if (rs.next()) {
            order.setId(rs.getInt(1));
        }
        rs.close();
        psInsert.close();

        // 4) Update stock availability
//        System.out.printf("buysell: %s\n", ( "'buy'".equalsIgnoreCase(buySellType) ? "- ?" : "+ ?" ));
        String sqlUpdateStock = 
            "UPDATE stock SET numShares = numShares "
          + ( "'buy'".equalsIgnoreCase(buySellType)
              ? "- ?"  
              : "+ ?" )
          + " WHERE stockSymbol = ?";
        psUpdateStock = con.prepareStatement(sqlUpdateStock);
        psUpdateStock.setInt(1, shares);
        psUpdateStock.setString(2, symbol);
        psUpdateStock.executeUpdate();

        con.commit();
        return "success";
    } catch (Exception e) {
        try { if (con != null) con.rollback(); } catch(Exception ex){}
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
            "SELECT o.orderID, o.dateTime, o.numShares, o.orderType, o.accountNum, o.employeeID, o.buySellType, o.pricePerShare, o.percentage " +
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
            } else if ("pricePerShare".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("pricePerShare"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("percentage"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("orderID"));
            o.setDatetime(rs.getTimestamp("dateTime"));
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
            "SELECT o.orderID,o.dateTime,o.numShares,o.orderType,o.buySellType,o.pricePerShare,o.percentage " +
            "FROM orders o " +
            "JOIN accounts a ON o.accountNum=a.accountNum " +
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
            } else if ("pricePerShare".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("pricePerShare"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("percentage"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("orderID"));
            o.setDatetime(rs.getTimestamp("dateTime"));
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
            "SELECT o.orderID,o.dateTime,o.numShares,o.orderType,o.buySellType,o.pricePerShare,o.percentage " +
            "FROM orders o " +
            "JOIN accounts a ON o.accountNum=a.accountNum " +
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
            } else if ("pricePerShare".equals(type)) {
                HiddenStopOrder h = new HiddenStopOrder();
                h.setPricePerShare(rs.getDouble("pricePerShare"));
                o = h;
            } else if ("trailingStop".equals(type)) {
                TrailingStopOrder t = new TrailingStopOrder();
                t.setPercentage(rs.getDouble("percentage"));
                o = t;
            } else {
                MarketOrder m = new MarketOrder();
                m.setBuySellType(rs.getString("buySellType"));
                o = m;
            }
            o.setId(rs.getInt("orderID"));
            o.setDatetime(rs.getTimestamp("dateTime"));
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
        		"SELECT o.orderID, o.stockSymbol, o.dateTime, o.orderType, o.pricePerShare, o.percentage, s.sharePrice " +
        			    "  FROM orders o " +
        			    "  JOIN stock   s ON o.stockSymbol = s.StockSymbol " +
        			    " WHERE o.stockSymbol = (" +
        			    "     SELECT stockSymbol " +
        			    "       FROM orders " +
        			    "      WHERE orderID = '" + orderId + "' LIMIT 1" +
        			    " )";
        rs = st.executeQuery(sql);
        while (rs.next()) {
            OrderPriceEntry e = new OrderPriceEntry();
            e.setOrderId       ( rs.getString("orderID") );
            e.setDate          ( rs.getTimestamp("dateTime") );
            e.setStockSymbol   ( rs.getString("stockSymbol") );
            double sharePrice  = rs.getDouble("sharePrice");
            e.setPricePerShare ( sharePrice );
            String type        = rs.getString("orderType");
            if ("pricePerShare".equals(type)) {
                e.setPrice(rs.getDouble("pricePerShare"));
            }
            else if ("trailingStop".equals(type)) {
                double pct = rs.getDouble("percentage");
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
