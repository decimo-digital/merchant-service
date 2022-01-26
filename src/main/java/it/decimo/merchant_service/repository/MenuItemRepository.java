package it.decimo.merchant_service.repository;

import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    /**
     * Recupera tutti i MenuItem dato il merchantId
     *
     * @param merchantId L'id del {@link Merchant} di cui ci interessa il menu
     */
    List<MenuItem> findAllByMerchantId(int merchantId);

    @Query(value = "SELECT max(item_id) FROM menu_item")
    int getCurrentMaxId();
}
