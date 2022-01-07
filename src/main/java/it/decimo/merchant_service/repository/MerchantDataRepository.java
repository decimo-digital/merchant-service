package it.decimo.merchant_service.repository;

import it.decimo.merchant_service.model.MerchantData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantDataRepository extends JpaRepository<MerchantData, Integer> {

    Optional<MerchantData> findByMerchantId(Integer merchantId);
}