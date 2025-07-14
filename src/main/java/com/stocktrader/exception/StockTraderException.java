package com.stocktrader.exception;

public class StockTraderException extends RuntimeException {
    
    public StockTraderException(String message) {
        super(message);
    }
    
    public StockTraderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public StockTraderException(Throwable cause) {
        super(cause);
    }
} 