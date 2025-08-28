package com.example.productcatalogapi.service;

import com.example.productcatalogapi.dto.ProductRequestDTO;
import com.example.productcatalogapi.dto.ProductResponseDTO;
import com.example.productcatalogapi.entity.Product;
import com.example.productcatalogapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //Create a new product
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO){
        //Check if product code already exists
        if(productRepository.existsByCode(requestDTO.getCode())){
            throw new DuplicateProductException("Product with code '" + requestDTO.getCode() + "' already exists");
        }
            Product product = convertToEntity(requestDTO);
            Product savedProduct = productRepository.save(product);
            return convertToResponseDTO(savedProduct);
    }


    //Get all products with pagination
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::convertToResponseDTO);
    }


    //Get product by ID
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: "+ id));
        return convertToResponseDTO(product);
    }


    //Get product by code
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with code: " + code));
        return convertToResponseDTO(product);
    }


    //Update product
    @Transactional(readOnly = true)
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO){
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id: " + id));

        //Check if code is being changed and if new code already exists
        if(!existingProduct.getCode().equals(requestDTO.getCode()) &&
                productRepository.existsByCode(requestDTO.getCode())){
                throw  new DuplicateProductException("Product with code '" +
                        requestDTO.getCode() +"' already exists");
        }

        updateProductFromDTO(existingProduct, requestDTO);
        Product updateProduct = productRepository.save(existingProduct);
        return convertToResponseDTO(updateProduct);
    }


    //Delete product
    public void deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }


    //Search product by name or description
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable){
        Page<Product> productPage = productRepository.searchByNameOrDescription(searchTerm, pageable);
        return productPage.map(this::convertToResponseDTO);
    }

    //Get products by category
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getProductsByCategory(String category, Pageable pageable){
        productRepository.findByCategoryIgnoreCase(category, pageable);
        return productPage.map(this::convertToResponseDTO);
    }


    //Get products by price range
    @Transactional(readOnly = true)
    public  Page<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable){
        Page<Product> productPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        return productPage.map(this::convertToResponseDTO);
    }


    //Get active products
    @Transactional(readOnly = true)
    public  Page<ProductResponseDTO> getActiveProducts(Pageable pageable){
        Page<Product> productPage = productRepository.findByIsActiveTrue(pageable);
        return productPage.map(this::convertToResponseDTO);
    }


    //Get all categories
    @Transactional(readOnly = true)
    public List<String> getAllCategories(){
        return productRepository.findAllCategories();
    }


    //Get low stock products
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockProducts(Integer threshold){
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    //Helper method to convert ProductResponseDTO to Product entity
    private Product convertToEntity(ProductResponseDTO requestDTO){
        Product product = new Product();
        product.setCode(requestDTO.getCode());
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setCategory(requestDTO.getCategory());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setIsActive(requestDTO.getIsActive());
        return product;
    }


    //Helper method to convert Product entity to ProductResponseDTO
    private ProductResponseDTO convertToResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }


    //Helper method to update existing product from ProductRequestDTO
    private void updateProductFromDTO(Product product, ProductRequestDTO requestDTO) {
        product.setCode(requestDTO.getCode());
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setCategory(requestDTO.getCategory());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setIsActive(requestDTO.getIsActive());
    }
}
