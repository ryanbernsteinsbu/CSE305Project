package com.stocktrader.controller;

import com.stocktrader.dao.StockDao;
import com.stocktrader.model.Stock;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet implementation class ViewGetStockPriceHistory
 */
public class GetStockPriceHistoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStockPriceHistoryController() {
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
		StockDao stockDao = new StockDao();
		List<Stock> stocks = stockDao.getStockPriceHistory(stockSymbol);

		request.setAttribute("stocks", stocks);
		request.setAttribute("heading", "Stock price history");

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/customer/showStocks.jsp");
        rd.forward(request, response);
	}

}
