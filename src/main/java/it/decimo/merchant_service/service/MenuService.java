package it.decimo.merchant_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.repository.MenuItemRepository;
import it.decimo.merchant_service.repository.MerchantRepository;

@Service
public class MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Controlla se esiste un locale con l'id specificato
     * 
     * @param id L'id che ci interessa controllare
     */
    public boolean doesMerchantExists(int id) {
        return merchantRepository.findById(id) != null;
    }

    /**
     * Recupera il menu per il locale specificato
     * 
     * @param merchantId Il locale per cui ci interessa il Menu
     */
    public List<MenuItem> getMenu(int merchantId) {
        logger.info("Getting menu of {}", merchantId);
        return menuItemRepository.findById_MerchantId(merchantId);
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
        return menuItemRepository.save(item);
    }

    /**
     * Rimuove un oggetto dal menu del locale, se è effettivamente suo
     */
    public void deleteItem(int menuItemId, int merchantId) {
        logger.info("Deleted item {}-{}", menuItemId, merchantId);
        menuItemRepository.deleteItem(menuItemId, merchantId);
    }
}