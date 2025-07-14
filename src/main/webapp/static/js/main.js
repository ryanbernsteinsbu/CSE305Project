// Stock Trading System - Main JavaScript

document.addEventListener('DOMContentLoaded', function() {
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Form validation
    initializeFormValidation();
    
    // Stock price updates
    initializeStockPriceUpdates();
    
    // Order type handling
    initializeOrderTypeHandling();
    
    // Auto-refresh functionality
    initializeAutoRefresh();
});

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// Stock price updates
function initializeStockPriceUpdates() {
    const stockPriceElements = document.querySelectorAll('.stock-price');
    
    stockPriceElements.forEach(element => {
        const price = parseFloat(element.textContent.replace(/[^0-9.-]+/g, ''));
        if (price < 0) {
            element.classList.add('negative');
        }
    });
}

// Order type handling
function initializeOrderTypeHandling() {
    const orderTypeSelect = document.getElementById('orderType');
    if (orderTypeSelect) {
        orderTypeSelect.addEventListener('change', function() {
            const selectedType = this.value;
            const percentageGroup = document.getElementById('orderPercentage-group');
            const priceGroup = document.getElementById('orderPricePerShare-group');
            
            // Hide all conditional fields
            if (percentageGroup) percentageGroup.classList.add('d-none');
            if (priceGroup) priceGroup.classList.add('d-none');
            
            // Show relevant fields based on order type
            switch(selectedType) {
                case 'TrailingStop':
                    if (percentageGroup) percentageGroup.classList.remove('d-none');
                    break;
                case 'HiddenStop':
                    if (priceGroup) priceGroup.classList.remove('d-none');
                    break;
            }
        });
    }
}

// Auto-refresh functionality
function initializeAutoRefresh() {
    const refreshElements = document.querySelectorAll('[data-refresh]');
    
    refreshElements.forEach(element => {
        const interval = element.getAttribute('data-refresh');
        if (interval) {
            setInterval(() => {
                refreshElement(element);
            }, parseInt(interval) * 1000);
        }
    });
}

// Refresh element content
function refreshElement(element) {
    const url = element.getAttribute('data-url');
    if (url) {
        fetch(url)
            .then(response => response.text())
            .then(data => {
                element.innerHTML = data;
            })
            .catch(error => {
                console.error('Error refreshing element:', error);
            });
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatPercentage(value) {
    return new Intl.NumberFormat('en-US', {
        style: 'percent',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value / 100);
}

function showNotification(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
}

// Stock search functionality
function initializeStockSearch() {
    const searchInput = document.getElementById('stockSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const query = this.value.toLowerCase();
            const stockRows = document.querySelectorAll('.stock-row');
            
            stockRows.forEach(row => {
                const symbol = row.querySelector('.stock-symbol').textContent.toLowerCase();
                const name = row.querySelector('.stock-name').textContent.toLowerCase();
                
                if (symbol.includes(query) || name.includes(query)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }
}

// Portfolio calculation
function calculatePortfolioValue() {
    const stockRows = document.querySelectorAll('.portfolio-stock');
    let totalValue = 0;
    
    stockRows.forEach(row => {
        const shares = parseFloat(row.querySelector('.stock-shares').textContent);
        const price = parseFloat(row.querySelector('.stock-price').textContent.replace(/[^0-9.-]+/g, ''));
        const value = shares * price;
        totalValue += value;
        
        // Update individual stock value
        const valueElement = row.querySelector('.stock-value');
        if (valueElement) {
            valueElement.textContent = formatCurrency(value);
        }
    });
    
    // Update total portfolio value
    const totalElement = document.getElementById('portfolioTotal');
    if (totalElement) {
        totalElement.textContent = formatCurrency(totalValue);
    }
    
    return totalValue;
}

// Export functions for global access
window.StockTrader = {
    formatCurrency,
    formatPercentage,
    showNotification,
    calculatePortfolioValue
}; 