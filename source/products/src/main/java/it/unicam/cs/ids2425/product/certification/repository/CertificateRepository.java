package it.unicam.cs.ids2425.product.certification.repository;

import it.unicam.cs.ids2425.product.certification.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
