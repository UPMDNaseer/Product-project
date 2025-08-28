package com.example.productcatalogapi.controller;

import com.example.productcatalogapi.dto.ProductRequestDTO;
import com.example.productcatalogapi.dto.ProductResponseDTO;
import com.example.productcatalogapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Tag(name = "Product Catalog API", description = "CRUD operations for product catalog management")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the catalog",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "409", description = "Product code already exists")
            }
    )
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO createProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createProduct, HttpStatus.CREATED);
    }



    @Operation(
            summary = "Get all products",
            description = "Retrieves all products with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products retrived successfully"),
            }
    )
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponseDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }



    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Get product by code", description = "Retrieves a product by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<ProductResponseDTO> getProducByCode(@PathVariable String code){
        ProductResponseDTO product = productService.getProductByCode(code);
        return ResponseEntity.ok(product);
    }


    @Operation(summary = "Updated product", description = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product code already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO updateProduct = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(updateProduct);
    }


    @Operation(summary = "Delete product", description = "Delete a product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product delete successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Search products", description = "Search products by name or description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(@Parameter(description = "search term")
                                                             @RequestParam String q,
                                                             @Parameter(description = "Page number (0 - based")
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @Parameter(description = "Page size")
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @Parameter(description = "Sort by field")
                                                             @RequestParam(defaultValue = "name") String soryBy,
                                                             @Parameter(description = "Sort direction (asc/desc)")
                                                             @RequestParam(defaultValue = "asc") String sortDir){
       Sort sort = sortDir.equalsIgnoreCase("desc") ?
               Sort.by(soryBy).descending() : Sort.by(soryBy).ascending();
       Pageable pageable = PageRequest.of(page, size, sort);
       Page<ProductResponseDTO> products = productService.searchProducts(q, pageable);
       return ResponseEntity.ok(products);
       }



    @Operation(summary = "Get products by category", description = "Retrieves products filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByCategory(
            @PathVariable String category,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponseDTO> products = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get products by price range", description = "Retrieves products within a specified price range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "price") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponseDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get active products", description = "Retrieves only active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active products retrieved successfully")
    })
    @GetMapping("/active")
    public ResponseEntity<Page<ProductResponseDTO>> getActiveProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponseDTO> products = productService.getActiveProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get all categories", description = "Retrieves all unique product categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get low stock products", description = "Retrieves products with stock quantity below threshold")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Low stock products retrieved successfully")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(
            @Parameter(description = "Stock threshold") @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductResponseDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
}
