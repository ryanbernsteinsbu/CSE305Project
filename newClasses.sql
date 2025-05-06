    CREATE TABLE customers (
        customerID BIGINT NOT NULL,
        firstName VARCHAR(50) NOT NULL,
        lastName VARCHAR(50) NOT NULL,
        address VARCHAR(100),
        city VARCHAR(50),
        state VARCHAR(20),
        zipCode VARCHAR(10),
        telephone VARCHAR(20),
        email VARCHAR(100),
        accountCreationDate DATE NOT NULL,
        creditCard VARCHAR(25),
        rating INT,
        CHECK (rating >= 1 AND rating <= 5),
        CONSTRAINT PK_Customers PRIMARY KEY (customerID)
    );

    CREATE TABLE employee (
        employeeID BIGINT NOT NULL,
        firstName VARCHAR(50) NOT NULL,
        lastName VARCHAR(50) NOT NULL,
        address VARCHAR(100),
        city VARCHAR(50),
        state VARCHAR(20),
        zipCode VARCHAR(10),
        telephone VARCHAR(20),
        startDate DATE NOT NULL,
        hourlyRate DECIMAL(7,2) NOT NULL,
        SSN VARCHAR(11),
        CHECK (hourlyRate >= 0),
        CONSTRAINT PK_Employee PRIMARY KEY (employeeID)
    );

    CREATE TABLE manager (
        managerID BIGINT NOT NULL,
        employeeID BIGINT NOT NULL,
        CONSTRAINT PK_Manager PRIMARY KEY (managerID),
        CONSTRAINT FK_Manager_Employee FOREIGN KEY (employeeID)
            REFERENCES employee (employeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );

    CREATE TABLE customerRep (
        repID BIGINT NOT NULL,
        employeeID BIGINT NOT NULL,
        CONSTRAINT PK_CustomerRep PRIMARY KEY (repID),
        CONSTRAINT FK_CustomerRep_Employee FOREIGN KEY (employeeID)
            REFERENCES employee (employeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );

    CREATE TABLE stock (
        stockSymbol VARCHAR(10) NOT NULL,
        stockName VARCHAR(100) NOT NULL,
        stockType VARCHAR(50),
        sharePrice DECIMAL(10,2) NOT NULL,
        numShares BIGINT NOT NULL,
        CHECK (numShares >= 0),
        CHECK (sharePrice >= 0),
        CONSTRAINT PK_Stock PRIMARY KEY (stockSymbol)
    );
    
        CREATE TABLE accounts (
        accountNum BIGINT NOT NULL,
        customerID BIGINT NOT NULL,
        CONSTRAINT PK_Account PRIMARY KEY (accountNum),
        CONSTRAINT FK_Account_Customer FOREIGN KEY (customerID)
            REFERENCES Customers (customerID)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );
	
    CREATE TABLE orders (
        orderID BIGINT NOT NULL,
        accountNum BIGINT NOT NULL,
        employeeID BIGINT,
        stockSymbol VARCHAR(10) NOT NULL,
        orderType VARCHAR(20),
        numShares INT NOT NULL,
        dateTime DATETIME NOT NULL,
        transactionFee DECIMAL(10,2),
        priceType VARCHAR(20),
        percentage INT,
        pricePerShare INT,
        CHECK (numShares >= 0),
        CHECK (TransactionFee >= 0),
        CONSTRAINT PK_StockOrder PRIMARY KEY (orderID),
        CONSTRAINT FK_StockOrder_Account FOREIGN KEY (accountNum)
            REFERENCES accounts (accountNum)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
        CONSTRAINT FK_StockOrder_Employee FOREIGN KEY (employeeID)
            REFERENCES employee (employeeID)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
        CONSTRAINT FK_StockOrder_Stock FOREIGN KEY (stockSymbol)
            REFERENCES stock (stockSymbol)
            ON DELETE NO ACTION
            ON UPDATE CASCADE
    );
    
	CREATE TABLE login (
		username VARCHAR(50) NOT NULL,
        password VARCHAR(50) NOT NULL,
		role VARCHAR(50) NOT NULL
    );
    
