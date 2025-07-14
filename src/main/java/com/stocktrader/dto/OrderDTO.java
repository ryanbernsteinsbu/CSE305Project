package com.stocktrader.dto;

import java.util.Date;

public class OrderDTO {
    private int id;
    private String customerId;
    private String employeeId;
    private String stockSymbol;
    private String orderType;
    private int numShares;
    private Date datetime;
    private double transactionFee;
    private String priceType;
    private Double percentage;
    private Double pricePerShare;
    
    // Constructors
    public OrderDTO() {}
    
    public OrderDTO(int id, String customerId, String employeeId, String stockSymbol, 
                   String orderType, int numShares, Date datetime, double transactionFee, 
                   String priceType, Double percentage, Double pricePerShare) {
        this.id = id;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.stockSymbol = stockSymbol;
        this.orderType = orderType;
        this.numShares = numShares;
        this.datetime = datetime;
        this.transactionFee = transactionFee;
        this.priceType = priceType;
        this.percentage = percentage;
        this.pricePerShare = pricePerShare;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    
    public int getNumShares() { return numShares; }
    public void setNumShares(int numShares) { this.numShares = numShares; }
    
    public Date getDatetime() { return datetime; }
    public void setDatetime(Date datetime) { this.datetime = datetime; }
    
    public double getTransactionFee() { return transactionFee; }
    public void setTransactionFee(double transactionFee) { this.transactionFee = transactionFee; }
    
    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    public Double getPricePerShare() { return pricePerShare; }
    public void setPricePerShare(Double pricePerShare) { this.pricePerShare = pricePerShare; }
} 