package it.decimo.merchant_service.repository;

import it.decimo.merchant_service.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    
    List<Merchant> findAllByOwner(int owner);
}
