<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="heading" value="Home"/>
<%@ include file="/header.jsp" %>
<div class="container mt-5">
  <h2 class="mb-4">Customer Options:</h2>
  <div class="row">
    <div class="col-md-6 mb-4">
      <div class="card h-100 shadow-sm animate__animated animate__fadeInLeft">
        <div class="card-header fw-bold">Orders</div>
        <div class="card-body">
          <a href="/stock-trader/viewAddOrder" class="btn btn-success w-100 mb-2 animate__animated animate__pulse animate__delay-1s">Place Order</a>
          <a href="/stock-trader/getOrdersByCustomer" class="btn btn-success w-100 animate__animated animate__pulse animate__delay-2s">Order History</a>
        </div>
      </div>
    </div>
    <div class="col-md-6 mb-4">
      <div class="card h-100 shadow-sm animate__animated animate__fadeInRight">
        <div class="card-header fw-bold">Stocks</div>
        <div class="card-body">
          <a href="/stock-trader/getStocksByCustomer" class="btn btn-success w-100 mb-2 animate__animated animate__pulse animate__delay-1s">Current stock holdings</a>
          <a href="/stock-trader/viewGetStockPriceHistory" class="btn btn-success w-100 mb-2 animate__animated animate__pulse animate__delay-2s">Stock price history</a>
          <a href="/stock-trader/viewSearchStocks" class="btn btn-success w-100 mb-2 animate__animated animate__pulse animate__delay-3s">Search stocks</a>
          <a href="/stock-trader/getCustomerBestsellers" class="btn btn-success w-100 mb-2 animate__animated animate__pulse animate__delay-4s">View bestseller stocks</a>
          <a href="/stock-trader/getStockSuggestions" class="btn btn-success w-100 animate__animated animate__pulse animate__delay-5s">View suggested stocks</a>
        </div>
      </div>
    </div>
  </div>
  <!-- Toast notification example -->
  <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
    <div id="liveToast" class="toast animate__animated animate__fadeInUp" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="toast-header">
        <strong class="me-auto">Stock Trader</strong>
        <small>Now</small>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
      <div class="toast-body">
        Welcome! Try the new modern UI and features.
      </div>
    </div>
  </div>
  <script src="https://cdn.jsdelivr.net/npm/animate.css@4.1.1/animate.min.css"></script>
  <script>
    window.onload = function() {
      var toastEl = document.getElementById('liveToast');
      if (toastEl) {
        var toast = new bootstrap.Toast(toastEl);
        toast.show();
      }
    };
  </script>
</div>
<%@ include file="/footer.jsp" %>