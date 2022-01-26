package it.decimo.merchant_service.service;

import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.model.MenuCategory;
import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.repository.MenuCategoryRepository;
import it.decimo.merchant_service.repository.MenuItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private MenuCategoryRepository menuCategoryRepository;
    @Autowired
    private MerchantService merchantService;


    /**
     * Recupera il menu per il locale specificato
     *
     * @param merchantId Il locale per cui ci interessa il Menu
     */
    public List<MenuItem> getMenu(int merchantId) {
        log.info("Getting menu of {}", merchantId);
        return menuItemRepository.findAllByMerchantId(merchantId);
    }

    /**
     * Salva un nuovo oggetto all'interno del menu di un dato locale
     *
     * @param merchantId Il locale a cui l'oggetto dev'essere collegato
     * @param item       L'oggetto da salvare
     * @return Il nuovo oggetto salvato nel DB. Ci interessa il suo Id
     */
    public MenuItem save(int merchantId, MenuItem item) {
        item.setMerchantId(merchantId);
        item.setMenuItemId(null);

        log.info("Saving item {}-{}", item.getMenuItemId(), merchantId);

        final var saved = menuItemRepository.save(item);
        log.info("Saved item {}-{}", saved.getMenuItemId(), merchantId);
        return saved;
    }

    /**
     * Rimuove un oggetto dal menu del locale, se è effettivamente suo
     */
    public void deleteItem(int menuItemId, int merchantId) {
        log.info("Deleting item {}-{}", menuItemId, merchantId);
        menuItemRepository.deleteById(menuItemId);

        final var stillExists = menuItemRepository.findById(menuItemId).isPresent();
        if (stillExists) {
            log.error("Failed to delete item {}", menuItemId);
        } else {
            log.info("Item {} deleted", menuItemId);
        }
    }

    /**
     * Aggiorna un oggetto del menu del locale con i dati nuovi
     *
     * @param merchantId Il locale a cui l'oggetto appartiene
     * @param item       L'oggetto da aggiornare
     * @param requester  chi ha richiesto l'aggiornamento
     */
    public ResponseEntity<Object> updateItem(int merchantId, MenuItem item, int requester) {
        if (!merchantService.isUserOwner(requester, merchantId)) {
            log.info("User {} is not owner of merchant {}", requester, merchantId);
            return ResponseEntity.status(401).body(new BasicResponse("You are not the owner of this merchant", "UNAUTHORIZED"));
        }

        final var oldItem = menuItemRepository.findById(item.getMenuItemId()).orElse(null);
        if (oldItem == null) {
            return ResponseEntity.status(404).body(new BasicResponse("Item not found", "NOT_FOUND"));
        }

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getPrice() != null) {
            oldItem.setPrice(item.getPrice());
        }
        if (item.getCategoryId() != null) {
            oldItem.setCategoryId(item.getCategoryId());
        }

        final var saved = menuItemRepository.save(oldItem);

        log.info("Item {} of merchant {} updated", saved.getMenuItemId(), merchantId);

        return ResponseEntity.ok().body(saved);
    }

    /**
     * Restituisce tutte le categorie di piatti
     */
    public List<MenuCategory> getCategories() {
        return menuCategoryRepository.findAll();
    }
}