package it.decimo.merchant_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.decimo.merchant_service.model.MerchantData;

@Repository
public interface MerchantDataRepository extends JpaRepository<MerchantData, Integer> {

    Optional<MerchantData> findByMerchantId(Integer merchantId);
}