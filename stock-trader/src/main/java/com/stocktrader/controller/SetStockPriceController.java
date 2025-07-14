package com.stocktrader.controller;

import com.stocktrader.dao.EmployeeDao;
import com.stocktrader.dao.LoginDao;
import com.stocktrader.dao.StockDao;
import com.stocktrader.model.Employee;
import com.stocktrader.model.Location;
import com.stocktrader.model.Login;
import com.stocktrader.model.Stock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet implementation class AddCustomerController
 */
public class SetStockPriceController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetStockPriceController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String stockSymbol = request.getParameter("stockSymbol");
		String stockPrice = request.getParameter("stockPrice");

		StockDao stockDao = new StockDao();
		String result = stockDao.setStockPrice(stockSymbol, Double.parseDouble(stockPrice));

		if(result.equals("success")) {
			String role = (String) request.getSession().getAttribute("role");
			if (role != null) {
				if (role.equals("manager")) {
					request.setAttribute("status", "success");
					request.getRequestDispatcher("/WEB-INF/views/admin/managerHome.jsp").forward(request, response);
				} else if (role.equals("customerRepresentative")) {
					request.setAttribute("status", "success");
					request.getRequestDispatcher("/WEB-INF/views/representative/customerRepresentativeHome.jsp").forward(request, response);
				} else {
					request.setAttribute("status", "success");
					request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
				}
			} else {
				response.sendRedirect("index.jsp");
			}
		} else {
			String role = (String) request.getSession().getAttribute("role");
			if (role != null) {
				if (role.equals("manager")) {
					request.setAttribute("status", "error");
					request.getRequestDispatcher("/WEB-INF/views/admin/managerHome.jsp").forward(request, response);
				} else if (role.equals("customerRepresentative")) {
					request.setAttribute("status", "error");
					request.getRequestDispatcher("/WEB-INF/views/representative/customerRepresentativeHome.jsp").forward(request, response);
				} else {
					request.setAttribute("status", "error");
					request.getRequestDispatcher("/WEB-INF/views/customer/home.jsp").forward(request, response);
				}
			} else {
				response.sendRedirect("index.jsp");
			}
		}
	}

}
