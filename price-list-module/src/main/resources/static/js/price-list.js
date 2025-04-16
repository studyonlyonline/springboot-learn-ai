/**
 * Price List Manager JavaScript
 * 
 * This file contains the JavaScript functionality for the price list manager.
 * Functions are designed to be generic and configurable.
 */

// Configuration object for the price list manager
const PriceListConfig = {
    // Selectors
    selectors: {
        searchInput: '#search-input',
        searchButton: '#search-button',
        productsTable: '#products-table',
        tableBody: '#products-table tbody',
        sortableHeaders: '.sortable',
        categoryFilters: '.category-filter',
        brandFilters: '.brand-filter',
        minPriceInput: '#min-price',
        maxPriceInput: '#max-price',
        applyPriceFilterButton: '#apply-price-filter',
        photoModal: '#photoModal',
        modalPhoto: '#modalPhoto',
        photoModalLabel: '#photoModalLabel',
        productThumbnails: '.product-thumbnail',
        barcodeIcons: '.barcode-icon',
        // Admin selectors
        addProductForm: '#addProductForm',
        editProductForm: '#editProductForm',
        editProductModal: '#editProductModal',
        editProductButtons: '.edit-product',
        deleteProductButtons: '.delete-product'
    },
    
    // API endpoints
    api: {
        search: window.location.pathname.includes('firestore') ? '/firestore-price-list/search' : '/price-list/search',
        autocomplete: window.location.pathname.includes('firestore') ? '/firestore-price-list/autocomplete' : '/price-list/autocomplete',
        // Admin API endpoints
        getProduct: (id) => `/firestore-price-list/products/${id}`,
        addProduct: '/firestore-price-list/products',
        updateProduct: (id) => `/firestore-price-list/products/${id}`,
        deleteProduct: (id) => `/firestore-price-list/products/${id}`
    },
    
    // Column indices for sorting
    columnIndices: {
        photo: 0,
        category: 1,
        brand: 2,
        name: 3,
        minPrice: 4,
        maxPrice: 5,
        stock: 6,
        barcode: 7
    },
    
    // Stock level thresholds
    stockLevels: {
        high: 30,
        medium: 15
    }
};

/**
 * Initialize the price list manager.
 * 
 * @param {Object} config - Configuration object
 */
function initPriceListManager(config) {
    $(document).ready(function() {
        // Initialize autocomplete
        initAutocomplete(config);
        
        // Initialize search functionality
        initSearch(config);
        
        // Initialize sorting functionality
        initSorting(config);
        
        // Initialize filtering functionality
        initFiltering(config);
        
        // Initialize photo modal functionality
        initPhotoModal(config);
        
        // Initialize barcode functionality
        initBarcodeHandling(config);
        
        // Initialize admin functionality if admin parameter is present
        if (new URLSearchParams(window.location.search).get('admin') === 'azsxazsx') {
            initAdminFunctionality(config);
        }
    });
}

/**
 * Initialize admin functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initAdminFunctionality(config) {
    // Initialize add product form
    $(config.selectors.addProductForm).submit(function(e) {
        e.preventDefault();
        
        const product = {
            name: $('#add-name').val(),
            category: $('#add-category').val(),
            brand: $('#add-brand').val(),
            minimumSellingPrice: parseFloat($('#add-min-price').val()),
            maximumSellingPrice: parseFloat($('#add-max-price').val()),
            stockAvailability: parseInt($('#add-stock').val()),
            photoUrl: $('#add-photo-url').val(),
            barcode: $('#add-barcode').val()
        };
        
        $.ajax({
            url: config.api.addProduct + '?admin=azsxazsx',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(product),
            success: function(data) {
                // Close modal
                $('#addProductModal').modal('hide');
                
                // Reset form
                $(config.selectors.addProductForm)[0].reset();
                
                // Reload page to show updated data
                window.location.reload();
            },
            error: function(xhr) {
                alert('Error adding product: ' + xhr.responseText);
            }
        });
    });
    
    // Initialize edit product button click
    $(document).on('click', config.selectors.editProductButtons, function() {
        const productId = $(this).data('product-id');
        const productName = $(this).data('product-name');
        const productCategory = $(this).data('product-category');
        const productBrand = $(this).data('product-brand');
        const productMinPrice = $(this).data('product-min-price');
        const productMaxPrice = $(this).data('product-max-price');
        const productStock = $(this).data('product-stock');
        const productPhotoUrl = $(this).data('product-photo-url');
        const productBarcode = $(this).data('product-barcode');
        
        // Populate form fields
        $('#edit-product-id').val(productId);
        $('#edit-name').val(productName);
        $('#edit-category').val(productCategory);
        $('#edit-brand').val(productBrand);
        $('#edit-min-price').val(productMinPrice);
        $('#edit-max-price').val(productMaxPrice);
        $('#edit-stock').val(productStock);
        $('#edit-photo-url').val(productPhotoUrl);
        $('#edit-barcode').val(productBarcode);
        
        // Show modal
        $(config.selectors.editProductModal).modal('show');
    });
    
    // Initialize edit product form submit
    $(config.selectors.editProductForm).submit(function(e) {
        e.preventDefault();
        
        const productId = $('#edit-product-id').val();
        const product = {
            id: productId,
            name: $('#edit-name').val(),
            category: $('#edit-category').val(),
            brand: $('#edit-brand').val(),
            minimumSellingPrice: parseFloat($('#edit-min-price').val()),
            maximumSellingPrice: parseFloat($('#edit-max-price').val()),
            stockAvailability: parseInt($('#edit-stock').val()),
            photoUrl: $('#edit-photo-url').val(),
            barcode: $('#edit-barcode').val()
        };
        
        $.ajax({
            url: config.api.updateProduct(productId) + '?admin=azsxazsx',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(product),
            success: function(data) {
                // Close modal
                $(config.selectors.editProductModal).modal('hide');
                
                // Reload page to show updated data
                window.location.reload();
            },
            error: function(xhr) {
                alert('Error updating product: ' + xhr.responseText);
            }
        });
    });
    
    // Initialize delete product button click
    $(document).on('click', config.selectors.deleteProductButtons, function() {
        const productId = $(this).data('product-id');
        
        if (confirm('Are you sure you want to delete this product?')) {
            $.ajax({
                url: config.api.deleteProduct(productId) + '?admin=azsxazsx',
                type: 'DELETE',
                success: function() {
                    // Reload page to show updated data
                    window.location.reload();
                },
                error: function(xhr) {
                    alert('Error deleting product: ' + xhr.responseText);
                }
            });
        }
    });
}

/**
 * Initialize photo modal functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initPhotoModal(config) {
    $(document).on('click', config.selectors.productThumbnails, function() {
        const photoUrl = $(this).data('photo-url');
        const productName = $(this).data('product-name');
        
        $(config.selectors.modalPhoto).attr('src', photoUrl);
        $(config.selectors.photoModalLabel).text(productName);
    });
}

/**
 * Initialize barcode handling functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initBarcodeHandling(config) {
    $(document).on('click', config.selectors.barcodeIcons, function() {
        const barcode = $(this).data('barcode');
        alert(`Barcode: ${barcode}\nThis would typically open a barcode scanner or display a barcode.`);
        // In a real implementation, this could open a barcode scanner or display the barcode
    });
}

/**
 * Initialize autocomplete functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initAutocomplete(config) {
    $(config.selectors.searchInput).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: config.api.autocomplete,
                dataType: "json",
                data: {
                    term: request.term
                },
                success: function(data) {
                    response(data);
                }
            });
        },
        minLength: 2,
        select: function(event, ui) {
            $(config.selectors.searchInput).val(ui.item.value);
            performSearch(config);
            return false;
        },
        focus: function(event, ui) {
            $(config.selectors.searchInput).val(ui.item.value);
            return false;
        }
    }).autocomplete("instance")._renderItem = function(ul, item) {
        return $("<li>")
            .append("<div>" + item.value + "</div>")
            .appendTo(ul);
    };
}

/**
 * Initialize search functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initSearch(config) {
    $(config.selectors.searchButton).click(function() {
        performSearch(config);
    });
    
    $(config.selectors.searchInput).keypress(function(e) {
        if (e.which === 13) {
            performSearch(config);
        }
    });
}

/**
 * Perform search based on the search input value.
 * 
 * @param {Object} config - Configuration object
 */
function performSearch(config) {
    const searchTerm = $(config.selectors.searchInput).val();
    
    $.ajax({
        url: config.api.search,
        dataType: "json",
        data: {
            query: searchTerm
        },
        success: function(data) {
            updateTable(data, config);
        }
    });
}

/**
 * Initialize sorting functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initSorting(config) {
    $(config.selectors.sortableHeaders).click(function() {
        const sortBy = $(this).data("sort");
        const rows = $(config.selectors.tableBody + " tr").toArray();
        
        // Remove asc/desc class from all headers
        $(config.selectors.sortableHeaders).not(this).removeClass("asc desc");
        
        rows.sort(function(a, b) {
            let aValue, bValue;
            const columnIndex = config.columnIndices[sortBy];
            
            if (sortBy === "category" || sortBy === "brand" || sortBy === "name") {
                aValue = $(a).find("td:eq(" + columnIndex + ")").text();
                bValue = $(b).find("td:eq(" + columnIndex + ")").text();
                return aValue.localeCompare(bValue);
            } else if (sortBy === "minPrice" || sortBy === "maxPrice") {
                aValue = parseFloat($(a).find("td:eq(" + columnIndex + ")").text().replace("$", ""));
                bValue = parseFloat($(b).find("td:eq(" + columnIndex + ")").text().replace("$", ""));
                return aValue - bValue;
            } else if (sortBy === "stock") {
                aValue = parseInt($(a).find("td:eq(" + columnIndex + ")").text());
                bValue = parseInt($(b).find("td:eq(" + columnIndex + ")").text());
                return aValue - bValue;
            }
        });
        
        // Toggle sort direction
        if ($(this).hasClass("asc")) {
            rows.reverse();
            $(this).removeClass("asc").addClass("desc");
        } else {
            $(this).removeClass("desc").addClass("asc");
        }
        
        // Remove all existing rows
        $(config.selectors.tableBody).empty();
        
        // Add sorted rows
        $.each(rows, function(index, row) {
            $(config.selectors.tableBody).append(row);
        });
    });
}

/**
 * Initialize filtering functionality.
 * 
 * @param {Object} config - Configuration object
 */
function initFiltering(config) {
    $(config.selectors.categoryFilters + ", " + config.selectors.brandFilters).change(function() {
        applyFilters(config);
    });
    
    $(config.selectors.applyPriceFilterButton).click(function() {
        applyFilters(config);
    });
}

/**
 * Apply filters to the table.
 * 
 * @param {Object} config - Configuration object
 */
function applyFilters(config) {
    const selectedCategories = $(config.selectors.categoryFilters + ":checked").map(function() {
        return $(this).val();
    }).get();
    
    const selectedBrands = $(config.selectors.brandFilters + ":checked").map(function() {
        return $(this).val();
    }).get();
    
    const minPrice = $(config.selectors.minPriceInput).val() ? parseFloat($(config.selectors.minPriceInput).val()) : 0;
    const maxPrice = $(config.selectors.maxPriceInput).val() ? parseFloat($(config.selectors.maxPriceInput).val()) : Number.MAX_VALUE;
    
    $(config.selectors.tableBody + " tr").each(function() {
        const category = $(this).find("td:eq(" + config.columnIndices.category + ")").text();
        const brand = $(this).find("td:eq(" + config.columnIndices.brand + ")").text();
        const price = parseFloat($(this).find("td:eq(" + config.columnIndices.minPrice + ")").text().replace("$", ""));
        
        const categoryMatch = selectedCategories.length === 0 || selectedCategories.includes(category);
        const brandMatch = selectedBrands.length === 0 || selectedBrands.includes(brand);
        const priceMatch = price >= minPrice && price <= maxPrice;
        
        if (categoryMatch && brandMatch && priceMatch) {
            $(this).show();
        } else {
            $(this).hide();
        }
    });
}

/**
 * Update the table with new data.
 * 
 * @param {Array} products - Array of product objects
 * @param {Object} config - Configuration object
 */
function updateTable(products, config) {
    const tbody = $(config.selectors.tableBody);
    tbody.empty();
    
    $.each(products, function(index, product) {
        const stockClass = getStockClass(product.stockAvailability, config);
        
        // Create photo cell
        const photoCell = $("<td>");
        if (product.photoUrl && product.photoUrl.trim() !== '') {
            const img = $("<img>")
                .addClass("product-thumbnail")
                .attr("src", product.photoUrl)
                .attr("alt", product.name)
                .attr("data-bs-toggle", "modal")
                .attr("data-bs-target", "#photoModal")
                .attr("data-photo-url", product.photoUrl)
                .attr("data-product-name", product.name);
            photoCell.append(img);
        } else {
            photoCell.append($("<span>").addClass("no-photo").text("No Photo"));
        }
        
        // Create barcode cell
        const barcodeCell = $("<td>");
        barcodeCell.append($("<span>").text(product.barcode || ""));
        if (product.barcode && product.barcode.trim() !== '') {
            barcodeCell.append(
                $("<i>")
                    .addClass("bi bi-upc-scan barcode-icon")
                    .attr("data-barcode", product.barcode)
            );
        }
        
        const row = $("<tr>")
            .append(photoCell)
            .append($("<td>").text(product.category))
            .append($("<td>").text(product.brand))
            .append($("<td>").text(product.name))
            .append($("<td>").text("$" + product.minimumSellingPrice.toFixed(2)))
            .append($("<td>").text("$" + product.maximumSellingPrice.toFixed(2)))
            .append($("<td>").addClass(stockClass).text(product.stockAvailability))
            .append(barcodeCell);
        
        // Add action buttons if in admin mode
        if (new URLSearchParams(window.location.search).get('admin') === 'azsxazsx') {
            const actionCell = $("<td>").addClass("action-buttons");
            
            // Add edit button
            const editButton = $("<button>")
                .addClass("btn btn-sm btn-warning edit-product")
                .attr("data-product-id", product.id)
                .attr("data-product-name", product.name)
                .attr("data-product-category", product.category)
                .attr("data-product-brand", product.brand)
                .attr("data-product-min-price", product.minimumSellingPrice)
                .attr("data-product-max-price", product.maximumSellingPrice)
                .attr("data-product-stock", product.stockAvailability)
                .attr("data-product-photo-url", product.photoUrl || "")
                .attr("data-product-barcode", product.barcode || "")
                .append($("<i>").addClass("bi bi-pencil"));
            
            // Add delete button
            const deleteButton = $("<button>")
                .addClass("btn btn-sm btn-danger delete-product")
                .attr("data-product-id", product.id)
                .append($("<i>").addClass("bi bi-trash"));
            
            actionCell.append(editButton).append(" ").append(deleteButton);
            row.append(actionCell);
        }
        
        tbody.append(row);
    });
    
    // Reapply filters after updating the table
    applyFilters(config);
}

/**
 * Get the CSS class for stock level.
 * 
 * @param {number} stockLevel - Stock level
 * @param {Object} config - Configuration object
 * @returns {string} CSS class
 */
function getStockClass(stockLevel, config) {
    if (stockLevel > config.stockLevels.high) {
        return 'stock-high';
    } else if (stockLevel > config.stockLevels.medium) {
        return 'stock-medium';
    } else {
        return 'stock-low';
    }
}

// Initialize the price list manager with the default configuration
initPriceListManager(PriceListConfig);
