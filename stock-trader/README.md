# Stock Trading System

A Java web application for managing stock trading operations with role-based access control.

> **Note**: This was originally a CSE305 group project that has been personalized and enhanced with additional features and improved architecture.

## 🏗️ Architecture

```
stock-trader/
├── src/main/java/com/stocktrader/
│   ├── controller/     # HTTP request handlers
│   ├── service/        # Business logic
│   ├── repository/     # Data access layer
│   ├── model/          # Domain objects
│   ├── config/         # Configuration
│   └── util/           # Utilities
├── src/main/webapp/
│   ├── WEB-INF/views/  # JSP pages by role
│   └── static/         # CSS, JS, assets
└── src/main/resources/sql/  # Database scripts
```

## 🎯 Features

- **Role-Based Access**: Managers, Customer Representatives, Customers
- **Stock Trading**: Market, Trailing Stop, Hidden Stop orders
- **Portfolio Management**: Track holdings and performance
- **Sales Analytics**: Reports and revenue tracking
- **Employee Management**: Add, edit, delete staff

## 🚀 Quick Start

### Prerequisites
- Java 8+, Maven 3.6+, MySQL 5.7+, Tomcat 8.5+

### Setup
```bash
# Database
mysql -u root -p < src/main/resources/sql/BETTERSCRIPT.sql
mysql -u root -p < src/main/resources/sql/basevalues.sql

# Build & Deploy
mvn clean install
mvn tomcat7:deploy

# Access
http://localhost:8080/stock-trader/
```

## 🔑 Login Credentials

- **Manager**: `dwarren@cs.sunysb.edu` / `admin789`
- **Customer Rep**: `dsmith@cs.sunysb.edu` / `rep456`
- **Customer**: `lewis.p@cs.sunysb.edu` / `password123`

## 🔧 Configuration

Update database settings in `src/main/java/com/stocktrader/config/DatabaseConfig.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/cse305";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

## 📊 Database Schema

- `Customers`: Customer info and ratings
- `Employee`: Staff data with roles
- `Stock`: Stock info and prices
- `Account`: Customer account mappings
- `StockOrder`: Trading transactions

## 🛠️ Development

### Adding Features
1. Create model in `model/`
2. Add repository methods in `repository/`
3. Implement business logic in `service/`
4. Create controller in `controller/`
5. Add JSP views in appropriate `views/` directory

## 🔒 Security

- Session-based authentication
- Role-based access control
- Input validation
- SQL injection prevention

## 📝 License

Educational project for CSE305 coursework.

---

**Built with ❤️ - Enhanced from original group project** 