package com.example.productcatalogapi.repository;

import com.example.productcatalogapi.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //Find by code (unique identifier)
    Optional<Product> findByCode(String code);

    //Check if product exits by code
    boolean existsByCode(String code);

    //Find product by name containing (case - insensitive)
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    //Find products by category
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    //Find products by price range
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    //Find active products
    Page<Product> findByIsActiveTrue(Pageable pageable);

    //Find products by active status
    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

    //Custom query: Find products by category and active status
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.isActive = :isActive")
    Page<Product> findByCategoryAndIsActive(@Param("category") String category,
                              @Param("isActive") Boolean isActive,
                              Pageable pageable);

    //Custom query: search products by name or description
    @Query("SELECT p FROM Product p WHERE " + "LOWER(p.name) LIKE LOWER(CONCAT('%', :serachTerm, '%'))")
    Page<Product> searchByNameOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    //Custom query: Find product with low stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    //Custom query: Get all categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();

    //Custom query: count products by category
    Long countByCategoryAndIsActiveTrue(@Param("category") String category);

    //Custom native query: Find products with stock quantity greater than specified value
    @Query(value = "SELECT * FROM products WHERE stock_quantity >?1 AND is_active = true", nativeQuery = true)
    List<Product> findProductsWithStockGreaterThan(Integer quantity);
}
