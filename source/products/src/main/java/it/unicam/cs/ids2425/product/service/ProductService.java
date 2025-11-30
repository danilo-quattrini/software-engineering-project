package it.unicam.cs.ids2425.product.service;

import it.unicam.cs.ids2425.product.Category;
import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.ProductStatus;
import it.unicam.cs.ids2425.notification.NotificationService;
import it.unicam.cs.ids2425.notification.NotificationType;
import it.unicam.cs.ids2425.product.certification.Certificate;
import it.unicam.cs.ids2425.product.certification.service.CertificateService;
import it.unicam.cs.ids2425.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService implements ProductOperation {
    private final ProductRepository productRepository;
    private final CertificateService certificateService;
    private final NotificationService notificationService;

    public ProductService(ProductRepository productRepository,
                          CertificateService certificateService,
                          NotificationService notificationService) {
        this.productRepository = productRepository;
        this.certificateService = certificateService;
        this.notificationService = notificationService;

    }

    /**
     * Return ONLY products created by the given user
     */
    @Override
    public List<Product> getProductByUser(Long id) {
        return productRepository.findByUserId(id);
    }

    /**
     * Return ALL products (Buyers will see everything)
     */
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    /**
     * Return only products that have been approved by a curator
     */
    @Override
    public List<Product> getApprovedProducts() {
        return productRepository.findByStatus(ProductStatus.APPROVED);
    }

    /**
     * Return products waiting for curation
     */
    @Override
    public List<Product> getPendingProducts() {
        return productRepository.findByStatus(ProductStatus.PENDING);
    }


    /**
     * Show detailed product info
     */
    public Product showProductInfo(UUID uuid) {
        return productRepository.findById(uuid).orElseThrow(()
                -> new RuntimeException("Product not found"));
    }

    /**
     * Create a new product
     */
    public void createNewProduct(Product product, MultipartFile file) throws IOException {
        Certificate certificate = certificateService.store(file);
        product.setCertificate(certificate);
        product.setStatus(ProductStatus.PENDING);
        if (product.getCertificate() != null) {
            product.getCertificate().setProduct(product);
        }
        productRepository.save(product);
    }


    /**
     * Delete a product
     */
    public void deleteProduct(UUID uuid) {
        Optional<Product> product = productRepository.findById(uuid);

        if (product.isEmpty()) throw new IllegalStateException("The product is not inside the Database.");

        productRepository.delete(product.get());
    }


    /**
     * Update a product
     */
    @Transactional
    public void updateProduct(UUID uuid, Product product, MultipartFile file) throws IOException {
        Product productFound = productRepository.findById(uuid).orElseThrow(() -> new IllegalStateException("The product you are trying to update doesn't exists"));
        Certificate certificate = certificateService.getFile(productFound.getCertificate().getId());
        certificateService.updateFile(file, certificate);

        productFound.setName(product.getName());
        productFound.setPrice(product.getPrice());
        productFound.setQuantity(product.getQuantity());
        productFound.setCategory(product.getCategory());
        productFound.setCertificate(certificate);
        productFound.setDescription(product.getDescription());
        productFound.setProductionPhases(product.getProductionPhases());
        productFound.setExpireDate(product.getExpireDate());
        productFound.setStatus(ProductStatus.PENDING);
        productRepository.save(productFound);
    }

    @Override
    public void approveProduct(UUID uuid) {
        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("The product you are trying to approve doesn't exist"));
        product.setStatus(ProductStatus.APPROVED);
        productRepository.save(product);
        notificationService.notifyProductDecision(product, NotificationType.PRODUCT_APPROVED);
    }

    @Override
    public void rejectProduct(UUID uuid) {
        Product product = productRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("The product you are trying to reject doesn't exist"));
        product.setStatus(ProductStatus.REJECTED);
        productRepository.save(product);
        notificationService.notifyProductDecision(product, NotificationType.PRODUCT_REJECTED);
    }


    /**
     * List of all the Product Categories
     */
    @Override
    public List<Category> getCategoryList() {
        return Arrays.asList(Category.BIO, Category.KM_ZERO, Category.DOP, Category.IGP);
    }

}
