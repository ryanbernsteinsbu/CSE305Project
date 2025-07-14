package com.stocktrader.service;

import com.stocktrader.model.Stock;
import com.stocktrader.repository.StockDao;
import com.stocktrader.exception.StockTraderException;

import java.util.List;

public class StockService {
    
    private final StockDao stockDao;
    
    public StockService() {
        this.stockDao = new StockDao();
    }
    
    public List<Stock> getAllStocks() {
        try {
            return stockDao.getAllStocks();
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve stocks", e);
        }
    }
    
    public List<Stock> getActivelyTradedStocks() {
        try {
            return stockDao.getActivelyTradedStocks();
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve actively traded stocks", e);
        }
    }
    
    public Stock getStockBySymbol(String symbol) {
        try {
            return stockDao.getStockBySymbol(symbol);
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve stock: " + symbol, e);
        }
    }
    
    public String setStockPrice(String symbol, double price) {
        try {
            if (price < 0) {
                throw new StockTraderException("Stock price cannot be negative");
            }
            return stockDao.setStockPrice(symbol, price);
        } catch (Exception e) {
            throw new StockTraderException("Failed to set stock price for: " + symbol, e);
        }
    }
    
    public List<Stock> getBestsellers() {
        try {
            return stockDao.getOverallBestsellers();
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve bestsellers", e);
        }
    }
    
    public List<Stock> getCustomerBestsellers(String customerId) {
        try {
            return stockDao.getCustomerBestsellers(customerId);
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve customer bestsellers", e);
        }
    }
    
    public List<Stock> getStockSuggestions(String customerId) {
        try {
            return stockDao.getStockSuggestions(customerId);
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve stock suggestions", e);
        }
    }
    
    public List<Stock> getStocksByType(String type) {
        try {
            return stockDao.getStockByType(type);
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve stocks by type: " + type, e);
        }
    }
    
    public List<Stock> getStocksByName(String name) {
        try {
            return stockDao.getStocksByName(name);
        } catch (Exception e) {
            throw new StockTraderException("Failed to retrieve stocks by name: " + name, e);
        }
    }
} 