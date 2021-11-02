package it.decimo.merchant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.decimo.merchant_service.model.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Integer> {

}
