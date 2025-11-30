package it.unicam.cs.ids2425.product.service;

import it.unicam.cs.ids2425.product.Category;
import it.unicam.cs.ids2425.product.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProductOperation {
    List<Product> getProductByUser(Long id);

    List<Product> getAllProduct();

    List<Product> getApprovedProducts();

    List<Product> getPendingProducts();

    Product showProductInfo(UUID uuid);

    void createNewProduct(Product product, MultipartFile file) throws IOException;

    void deleteProduct(UUID uuid);

    void updateProduct(UUID uuid, Product product, MultipartFile file) throws IOException;

    void approveProduct(UUID uuid);

    void rejectProduct(UUID uuid);

    List<Category> getCategoryList();
}
