package it.unicam.cs.ids2425.payment.repository;

import it.unicam.cs.ids2425.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.reference.id = ?1")
    Optional<Payment> findByReferenceId(Long paymentId);

    @Query("SELECT p FROM Payment p WHERE p.processed = false")
    List<Payment> findByProcessedFalse();

    @Query("SELECT p FROM Payment p WHERE p.processed = true")
    List<Payment> findByProcessedTrue();
}
