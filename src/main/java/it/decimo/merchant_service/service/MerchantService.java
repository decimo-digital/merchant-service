package it.decimo.merchant_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.merchant_service.dto.Location;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.repository.MerchantRepository;
import it.decimo.merchant_service.util.Distance;

@Service
public class MerchantService {
    private static final Logger logger = LoggerFactory.getLogger(MerchantService.class);

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * Salva il merchant passato come parametro all'interno del DB
     * 
     * @param merchant il merchant da salvare
     * @return {@code null} se non è stato possibile salvare il merchant, altrimenti
     *         il suo {@code id}
     */
    public Integer saveMerchant(Merchant merchant) {
        try {
            logger.info("Saving merchant {}", merchant.getStoreName());
            return merchantRepository.save(merchant).getId();
        } catch (Exception e) {
            logger.error("Got error while saving merchant {}", merchant.getStoreName(), e);
            return null;
        }
    }

    /**
     * Ritorna la lista di esercenti
     * 
     * @param point  Opzionale -- definisce il centro dal quale recuperare i
     *               merchant. Se definito i merchant vengono ritornati ordinati
     * @param radius Opzionale -- definisce la larghezza del raggio entro il quale
     *               includere gli esercenti
     * @return La lista degli esercenti, opzionalmente ordinata (se point è
     *         definito)
     */
    public List<Merchant> getMerchants(Location point) {
        final var merchants = merchantRepository.findAll();

        if (point != null && (point.getX() != null && point.getY() != null)) {
            for (Merchant merchant : merchants) {
                final var merchantPosition = merchant.getPoint();
                final var distance = Distance.gps2m(merchantPosition, point.toPoint());
                merchant.setDistance(distance);
            }
            merchants.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));
        }

        return merchants;
    }

}
