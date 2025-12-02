package it.unicam.cs.ids2425.product.certification.service;

import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.certification.Certificate;
import it.unicam.cs.ids2425.product.certification.repository.CertificateRepository;
import it.unicam.cs.ids2425.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ProductRepository productRepository;

    public CertificateService(CertificateRepository certificateRepository, ProductRepository productRepository) {
        this.certificateRepository = certificateRepository;
        this.productRepository = productRepository;
    }

    /**
     * Store the file inside the database.
     *
     **/
    public Certificate store(MultipartFile file) throws IOException {
        Certificate certificate = new Certificate();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        certificate.setName(fileName);
        certificate.setType(file.getContentType());
        certificate.setData(file.getBytes());

        certificateRepository.save(certificate);

        return certificate;
    }

    /**
     * Get the file inside the database.
     *
     **/
    public Certificate getFile(Long id) {
        if (certificateRepository.findById(id).isEmpty()) throw new IllegalArgumentException("No certificate found");
        else return certificateRepository.findById(id).get();
    }

    /**
     * Delete the file inside the database.
     **/
    public void removeFile(Long id) {
        Product product = productRepository.findById(getFile(id).getProduct().getId()).orElseThrow();
        product.setCertificate(null);
        certificateRepository.delete(getFile(id));
    }

    /**
     * Update the file inside the database.
     **/
    public void updateFile(MultipartFile file, Certificate certificate) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        certificate.setName(fileName);
        certificate.setType(file.getContentType());
        certificate.setData(file.getBytes());

        certificateRepository.save(certificate);

    }
}
