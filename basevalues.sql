-- Insert Customers
INSERT INTO customers (customerID, firstName, lastName, address, city, state, zipCode, telephone, email, accountCreationDate, creditCard, rating)
VALUES 
(111111111, 'Shang', 'Yang', '123 Success Street', 'Stony Brook', 'NY', '11790', '516-632-8959', 'syang@cs.sunysb.edu', '2006-01-01', '1234567812345678', 1),
(222222222, 'Victor', 'Du', '456 Fortune Road', 'Stony Brook', 'NY', '11790', '516-632-4360', 'vicdu@cs.sunysb.edu', '2006-02-15', '5678123456781234', 1),
(333333333, 'John', 'Smith', '789 Peace Blvd.', 'Los Angeles', 'CA', '93536', '315-443-4321', 'jsmith@ic.sunysb.edu', '2006-03-10', '2345678923456789', 1),
(444444444, 'Lewis', 'Philip', '135 Knowledge Lane', 'Stony Brook', 'NY', '11794', '516-666-8888', 'pml@cs.sunysb.edu', '2006-04-05', '6789234567892345', 1);

-- Insert Accounts
INSERT INTO accounts (accountNum, customerID)
VALUES 
(1, 444444444),
(2, 222222222);

-- Insert Employees
INSERT INTO employee (employeeID, firstName, lastName, address, city, state, zipCode, telephone, startDate, hourlyRate, SSN)
VALUES 
(123456789, 'David', 'Smith', '123 College road', 'Stony Brook', 'NY', '11790', '516-215-2345', '2005-11-01', 60.00, '123-45-6789'),
(789123456, 'David', 'Warren', '456 Sunken Street', 'Stony Brook', 'NY', '11794', '631-632-9987', '2006-02-02', 50.00, '789-12-3456');

-- Insert Manager
INSERT INTO manager (managerID, employeeID)
VALUES (1, 789123456);

-- Insert Customer Representatives
INSERT INTO customerRep (repID, employeeID)
VALUES (1, 123456789);

-- Insert Stocks
INSERT INTO stock (stockSymbol, stockName, stockType, sharePrice, numShares)
VALUES 
('GM', 'General Motors', 'automotive', 34.23, 1000),
('IBM', 'IBM', 'computer', 91.41, 500),
('F', 'Ford', 'automotive', 9.00, 750);

-- Insert Orders
INSERT INTO orders (orderID, accountNum, employeeID, stockSymbol, orderType, numShares, dateTime, transactionFee, priceType, percentage, pricePerShare)
VALUES 
(1, 1, 123456789, 'GM', 'buy', 75, '2025-04-01 10:30:00', 0.00, 'market', NULL, NULL),
(2, 2, 123456789, 'IBM', 'sell', 10, '2025-04-02 14:15:00', 0.00, 'trailing stop', 15, NULL),
(3, 2, 123456789, 'IBM', 'sell', 10, '2025-04-03 09:45:00', 0.00, 'market on close', NULL, NULL);

-- Insert Login Data
INSERT INTO login (username, password, role)
VALUES 
('lewis.p@cs.sunysb.edu', 'password123', 'customer'),
('dsmith@cs.sunysb.edu', 'rep456', 'customerRepresentative'),
('dwarren@cs.sunysb.edu', 'admin789', 'manager');
