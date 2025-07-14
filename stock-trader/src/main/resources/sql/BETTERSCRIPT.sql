    CREATE TABLE Customers (
        CustomerID BIGINT NOT NULL,
        FirstName VARCHAR(50) NOT NULL,
        LastName VARCHAR(50) NOT NULL,
        Address VARCHAR(100),
        City VARCHAR(50),
        State VARCHAR(20),
        ZipCode VARCHAR(10),
        Telephone VARCHAR(20),
        Email VARCHAR(100),
        EmailAddress VARCHAR(100),
        AccountCreationDate DATE NOT NULL,
        CreditCardNumber VARCHAR(25),
        Rating INT,
        CHECK (Rating >= 1 AND Rating <= 5),
        CONSTRAINT PK_Customers PRIMARY KEY (CustomerID)
    );

    CREATE TABLE Employee (
        EmployeeID BIGINT NOT NULL,
        FirstName VARCHAR(50) NOT NULL,
        LastName VARCHAR(50) NOT NULL,
        Address VARCHAR(100),
        City VARCHAR(50),
        State VARCHAR(20),
        ZipCode VARCHAR(10),
        Telephone VARCHAR(20),
        StartDate DATE NOT NULL,
        HourlyRate DECIMAL(7,2) NOT NULL,
        SSN VARCHAR(11),
        CHECK (HourlyRate >= 0),
        CONSTRAINT PK_Employee PRIMARY KEY (EmployeeID)
    );

    CREATE TABLE Manager (
        ManagerID BIGINT NOT NULL,
        EmployeeID BIGINT NOT NULL,
        CONSTRAINT PK_Manager PRIMARY KEY (ManagerID),
        CONSTRAINT FK_Manager_Employee FOREIGN KEY (EmployeeID)
            REFERENCES Employee (EmployeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );

    CREATE TABLE CustomerRep (
        RepID BIGINT NOT NULL,
        EmployeeID BIGINT NOT NULL,
        CONSTRAINT PK_CustomerRep PRIMARY KEY (RepID),
        CONSTRAINT FK_CustomerRep_Employee FOREIGN KEY (EmployeeID)
            REFERENCES Employee (EmployeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );

    CREATE TABLE Stock (
        StockSymbol VARCHAR(10) NOT NULL,
        StockName VARCHAR(100) NOT NULL,
        StockType VARCHAR(50),
        SharePrice DECIMAL(10,2) NOT NULL,
        NumberOfShares BIGINT NOT NULL,
        CHECK (NumberOfShares >= 0),
        CHECK (SharePrice >= 0),
        CONSTRAINT PK_Stock PRIMARY KEY (StockSymbol)
    );

    CREATE TABLE Account (
        AccountNumber BIGINT NOT NULL,
        CustomerID BIGINT NOT NULL,
        CONSTRAINT PK_Account PRIMARY KEY (AccountNumber),
        CONSTRAINT FK_Account_Customer FOREIGN KEY (CustomerID)
            REFERENCES Customers (CustomerID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );

    CREATE TABLE StockOrder (
        OrderID BIGINT NOT NULL,
        AccountNumber BIGINT NOT NULL,
        EmployeeID BIGINT NOT NULL,
        StockSymbol VARCHAR(10) NOT NULL,
        OrderType VARCHAR(20) NOT NULL,
        NumberOfShares INT NOT NULL,
        DateTime DATETIME NOT NULL,
        TransactionFee DECIMAL(10,2),
        PriceType VARCHAR(20),
        CHECK (NumberOfShares >= 0),
        CHECK (TransactionFee >= 0),
        CONSTRAINT PK_StockOrder PRIMARY KEY (OrderID),
        CONSTRAINT FK_StockOrder_Account FOREIGN KEY (AccountNumber)
            REFERENCES Account (AccountNumber)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
        CONSTRAINT FK_StockOrder_Employee FOREIGN KEY (EmployeeID)
            REFERENCES Employee (EmployeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
        CONSTRAINT FK_StockOrder_Stock FOREIGN KEY (StockSymbol)
            REFERENCES Stock (StockSymbol)
            ON DELETE NO ACTION
            ON UPDATE CASCADE
    );

    -- Insert Clients
    INSERT INTO Customers (CustomerID, FirstName, LastName, Address, City, State, ZipCode, Telephone, EmailAddress, AccountCreationDate, CreditCardNumber, Rating)
    VALUES 
    (111111111, 'Shang', 'Yang', '123 Success Street', 'Stony Brook', 'NY', '11790', '516-632-8959', 'syang@cs.sunysb.edu', '2006-01-01', '1234567812345678', 1),
    (222222222, 'Victor', 'Du', '456 Fortune Road', 'Stony Brook', 'NY', '11790', '516-632-4360', 'vicdu@cs.sunysb.edu', '2006-02-15', '5678123456781234', 1),
    (333333333, 'John', 'Smith', '789 Peace Blvd.', 'Los Angeles', 'CA', '93536', '315-443-4321', 'jsmith@ic.sunysb.edu', '2006-03-10', '2345678923456789', 1),
    (444444444, 'Lewis', 'Philip', '135 Knowledge Lane', 'Stony Brook', 'NY', '11794', '516-666-8888', 'pml@cs.sunysb.edu', '2006-04-05', '6789234567892345', 1);

    -- Insert Accounts
    INSERT INTO Account (AccountNumber, CustomerID)
    VALUES 
    (1, 444444444),
    (2, 222222222);

    -- Insert Employees
    INSERT INTO Employee (EmployeeID, FirstName, LastName, Address, City, State, ZipCode, Telephone, StartDate, HourlyRate, SSN)
    VALUES 
    (123456789, 'David', 'Smith', '123 College road', 'Stony Brook', 'NY', '11790', '516-215-2345', '2005-11-01', 60.00, 123456789),
    (789123456, 'David', 'Warren', '456 Sunken Street', 'Stony Brook', 'NY', '11794', '631-632-9987', '2006-02-02', 50.00, 789123456);

    -- Insert Manager (David Warren is the Manager)
    INSERT INTO Manager (ManagerID, EmployeeID)
    VALUES (1, 789123456);

    -- Insert CustomerRep (insert employees into customer reps
    INSERT INTO CustomerRep(RepID, EmployeeID)
    VALUES (1, 123456789);

    -- Insert Stocks
    INSERT INTO Stock (StockSymbol, StockName, StockType, SharePrice, NumberOfShares)
    VALUES 
    ('GM', 'General Motors', 'automotive', 34.23, 1000),
    ('IBM', 'IBM', 'computer', 91.41, 500),
    ('F', 'Ford', 'automotive', 9.00, 750);

    -- Insert Orders
    INSERT INTO StockOrder (OrderID, AccountNumber, EmployeeID, StockSymbol, OrderType, NumberOfShares, DateTime, TransactionFee, PriceType)
    VALUES 
    (1, 1, 123456789, 'GM', 'buy', 75, '2025-04-01 10:30:00', 0.00, 'market'),
    (2, 2, 123456789, 'IBM', 'sell', 10, '2025-04-02 14:15:00', 0.00, 'trailing stop 10%'),
    (3, 2, 123456789, 'IBM', 'sell', 10, '2025-04-03 09:45:00', 0.00, '$90');

