package com.example.productcatalogapi.service;

import com.example.productcatalogapi.dto.ProductRequestDTO;
import com.example.productcatalogapi.dto.ProductResponseDTO;
import com.example.productcatalogapi.entity.Product;
import com.example.productcatalogapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return convertToResponseDTO(product);

    }
}
