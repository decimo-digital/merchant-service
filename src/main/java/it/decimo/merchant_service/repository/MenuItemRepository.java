package it.decimo.merchant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.model.Merchant;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    /**
     * Recupera tutti i MenuItem dato il merchantId
     * 
     * @param merchantId L'id del {@link Merchant} di cui ci interessa il menu
     */
    @Query(value = "SELECT * FROM merchant_menu WHERE merchant_id = :merchantId", nativeQuery = true)
    List<MenuItem> findById_MerchantId(@Param(value = "merchantId") Integer merchantId);

    @Query(value = "DELETE FROM merchant_menu WHERE menu_item_id = :menuItemId and merchant_id = :merchant_id", nativeQuery = true)
    void deleteItem(@Param(value = "menuItemId") int menuItemId, @Param(value = "merchant_id") int merchantId);
}
