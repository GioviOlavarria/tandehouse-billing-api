package tande.house.billingapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tande.house.billingapi.model.Boleta;

import java.util.Optional;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    Optional<Boleta> findByCommerceOrder(String commerceOrder);
    Optional<Boleta> findTopByOrderByFolioDesc();
}
