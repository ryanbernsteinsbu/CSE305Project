package com.stocktrader.util;

import javax.servlet.http.HttpSession;

public class SessionManager {
    
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("email") != null;
    }
    
    public static String getUserRole(HttpSession session) {
        return (String) session.getAttribute("role");
    }
    
    public static String getUserEmail(HttpSession session) {
        return (String) session.getAttribute("email");
    }
    
    public static boolean isManager(HttpSession session) {
        return "manager".equals(getUserRole(session));
    }
    
    public static boolean isCustomerRepresentative(HttpSession session) {
        return "customerRepresentative".equals(getUserRole(session));
    }
    
    public static boolean isCustomer(HttpSession session) {
        return "customer".equals(getUserRole(session));
    }
    
    public static void logout(HttpSession session) {
        session.invalidate();
    }
    
    public static void setUserSession(HttpSession session, String email, String role) {
        session.setAttribute("email", email);
        session.setAttribute("role", role);
    }
} 