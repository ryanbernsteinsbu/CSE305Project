-- Manager-Level Transactions

-- 1. Set the share price of a stock TESTED
BEGIN TRANSACTION SetSharePrice;
UPDATE Stock SET SharePrice = ? WHERE StockSymbol = ?;
COMMIT;

-- 2. Add an employee TESTED
BEGIN TRANSACTION AddEmployee;
INSERT INTO Employee (EmployeeID, FirstName, LastName, Address, City, State, ZipCode, Telephone, StartDate, HourlyRate, SSN)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
COMMIT;

-- 3. Edit employee information TESTED
BEGIN TRANSACTION EditEmployeeInfo;
UPDATE Employee SET StartDate = ?, HourlyRate = ? WHERE EmployeeID = ?;
COMMIT;

-- 4. Delete an employee TESTED
BEGIN TRANSACTION DeleteEmployee;
DELETE FROM Employee WHERE EmployeeID = ?;
COMMIT;

-- 5. Obtain a sales report for a particular month TESTED
BEGIN TRANSACTION SalesReport;
SELECT Stock.StockSymbol, SUM(StockOrder.NumberOfShares * Stock.SharePrice)
FROM StockOrder, Stock
WHERE strftime('%m', DateTime) = ? AND strftime('%Y', DateTime) = ?
GROUP BY Stock.StockSymbol;
COMMIT;

-- 6. Produce a comprehensive listing of all stocks TESTED
BEGIN TRANSACTION ListAllStocks;
SELECT * FROM Stock;
COMMIT;

-- 7. Produce a list of orders by stock symbol or by customer name TESTED
BEGIN TRANSACTION ListOrdersByStockOrCustomer;
SELECT * FROM StockOrder WHERE StockSymbol = ? OR AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = (SELECT CustomerID FROM Customers WHERE LastName = ?));
COMMIT;

-- 8. Produce a summary listing of revenue by stock, stock type, or customer TESTED
BEGIN TRANSACTION RevenueByStock;
SELECT StockSymbol, SUM(StockOrder.NumberOfShares * (SELECT SharePrice FROM Stock WHERE Stock.StockSymbol = StockOrder.StockSymbol)) AS Revenue
FROM StockOrder
GROUP BY StockSymbol;
COMMIT;

BEGIN TRANSACTION RevenueByStockType;
SELECT StockType, SUM(StockOrder.NumberOfShares * Stock.SharePrice) AS Revenue
FROM Stock
JOIN StockOrder ON Stock.StockSymbol = StockOrder.StockSymbol
GROUP BY Stock.StockType;
COMMIT;

BEGIN TRANSACTION RevenueByCustomer;
SELECT CustomerID, SUM(NumberOfShares * (SELECT SharePrice FROM Stock WHERE Stock.StockSymbol = StockOrder.StockSymbol)) AS Revenue
FROM StockOrder
JOIN Account ON StockOrder.AccountNumber = Account.AccountNumber
GROUP BY CustomerID;
COMMIT;

-- 9. Determine which customer representative generated most revenue TESTED
BEGIN TRANSACTION MostRevenueGeneratedByRep;
SELECT StockOrder.EmployeeID, SUM(NumberOfShares * (SELECT SharePrice FROM Stock WHERE Stock.StockSymbol = StockOrder.StockSymbol)) AS TotalRevenue
FROM StockOrder
JOIN Employee ON StockOrder.EmployeeID = Employee.EmployeeID
GROUP BY StockOrder.EmployeeID
ORDER BY TotalRevenue DESC
LIMIT 1;
COMMIT;

-- 10. Determine which customer generated most revenue TESTED
BEGIN TRANSACTION MostRevenueGeneratedByCustomer;
SELECT CustomerID, SUM(NumberOfShares * (SELECT SharePrice FROM Stock WHERE Stock.StockSymbol = StockOrder.StockSymbol)) AS TotalRevenue
FROM StockOrder
JOIN Account ON StockOrder.AccountNumber = Account.AccountNumber
GROUP BY CustomerID
ORDER BY TotalRevenue DESC
LIMIT 1;
COMMIT;

-- 11. Produce a list of most actively traded stocks TESTED
BEGIN TRANSACTION MostActivelyTradedStocks;
SELECT StockSymbol, COUNT(*) AS TradeCount
FROM StockOrder
GROUP BY StockSymbol
ORDER BY TradeCount DESC;
COMMIT;

-- 1. Record an order
BEGIN TRANSACTION RecordOrder;
INSERT INTO StockOrder (OrderID, AccountNumber, EmployeeID, StockSymbol, OrderType, NumberOfShares, DateTime, TransactionFee, PriceType)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
COMMIT;

-- 2. Add a customer
BEGIN TRANSACTION AddCustomer;
INSERT INTO Customers (CustomerID, FirstName, LastName, Address, City, State, ZipCode, Telephone, EmailAddress, AccountCreationDate, CreditCardNumber, Rating)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
COMMIT;

-- 3. Edit customer information TESTED
BEGIN TRANSACTION EditCustomerInfo;
UPDATE Customers SET EmailAddress = ?, Rating = ?, CreditCardNumber = ? WHERE CustomerID = ?;
COMMIT;

-- 4. Delete a customer TESTED
BEGIN TRANSACTION DeleteCustomer;
DELETE FROM Customers WHERE CustomerID = ?;
COMMIT;

-- 5. Produce customer mailing lists TESTED
BEGIN TRANSACTION CustomerMailingList;
SELECT EmailAddress FROM Customers;
COMMIT;

-- 6. Produce a list of stock suggestions for a given customer
BEGIN TRANSACTION StockSuggestionsForCustomer;
SELECT DISTINCT StockSymbol
FROM StockOrder
WHERE AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?);
COMMIT;

-- 1. Retrieve customer's current stock holdings
BEGIN TRANSACTION CustomerStockHoldings;
SELECT StockSymbol, SUM(NumberOfShares) AS TotalShares
FROM StockOrder
WHERE AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?)
GROUP BY StockSymbol;
COMMIT;

-- 2. Retrieve share-price and trailing-stop history
BEGIN TRANSACTION TrailingStopHistory;
SELECT * FROM StockOrder WHERE OrderType = ? AND AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?);
COMMIT;

-- 3. Retrieve share-price and hidden-stop history
BEGIN TRANSACTION HiddenStopHistory;
SELECT * FROM StockOrder WHERE OrderType = ? AND AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?);
COMMIT;

-- 4. Retrieve share-price history of a stock over a certain period
BEGIN TRANSACTION StockPriceHistory;
SELECT Stock.StockSymbol, StockOrder.DateTime, Stock.SharePrice
FROM StockOrder
JOIN Stock ON StockOrder.StockSymbol = Stock.StockSymbol
WHERE StockOrder.StockSymbol = ? AND StockOrder.DateTime BETWEEN ? AND ?;
COMMIT;

-- 5. Retrieve history of all orders a customer has placed
BEGIN TRANSACTION CustomerOrderHistory;
SELECT * FROM StockOrder
WHERE AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?);
COMMIT;

-- 6. Retrieve stocks available of a particular type and most-recent order info
BEGIN TRANSACTION StocksByType;
SELECT * FROM Stock WHERE StockType = ?;
SELECT * FROM StockOrder WHERE StockSymbol IN (SELECT StockSymbol FROM Stock WHERE StockType = ?) ORDER BY DateTime DESC;
COMMIT;

-- 7. Retrieve stocks available with keywords in stock name
BEGIN TRANSACTION StocksByKeyword;
SELECT * FROM Stock WHERE StockName LIKE ?;
SELECT * FROM StockOrder WHERE StockSymbol IN (SELECT StockSymbol FROM Stock WHERE StockName LIKE ?) ORDER BY DateTime DESC;
COMMIT;

-- 8. Retrieve best-seller list of stocks
BEGIN TRANSACTION BestSellerStocks;
SELECT Stock.StockSymbol, SUM(StockOrder.NumberOfShares) AS TotalShares
FROM StockOrder
JOIN Stock ON StockOrder.StockSymbol = Stock.StockSymbol
GROUP BY Stock.StockSymbol
ORDER BY TotalShares DESC;
COMMIT;

-- 9. Retrieve personalized stock suggestion list
BEGIN TRANSACTION PersonalizedStockSuggestions;
SELECT DISTINCT StockSymbol
FROM StockOrder
WHERE AccountNumber IN (SELECT AccountNumber FROM Account WHERE CustomerID = ?)
ORDER BY StockSymbol;
COMMIT;
