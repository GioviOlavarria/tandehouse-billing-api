package tande.house.billingapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tande.house.billingapi.model.Boleta;
import tande.house.billingapi.repo.BoletaRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BoletaRepository repo;
    private static final double IVA_RATE = 0.19;

    private long nextFolio() {
        return repo.findTopByOrderByFolioDesc()
                .map(b -> b.getFolio() + 1)
                .orElse(1L);
    }

    @Transactional
    public Boleta create(String commerceOrder, int total) {
        return repo.findByCommerceOrder(commerceOrder)
                .orElseGet(() -> {
                    int neto = (int) Math.round(total / (1 + IVA_RATE));
                    int iva = total - neto;

                    Boleta b = new Boleta();
                    b.setCommerceOrder(commerceOrder);
                    b.setFolio(nextFolio());
                    b.setNeto(neto);
                    b.setIva(iva);
                    b.setTotal(total);
                    b.setCreatedAt(OffsetDateTime.now());

                    return repo.save(b);
                });
    }

    @Transactional(readOnly = true)
    public Boleta getByCommerceOrder(String commerceOrder) {
        return repo.findByCommerceOrder(commerceOrder)
                .orElseThrow(() -> new RuntimeException("Boleta no existe"));
    }
}
