package it.decimo.merchant_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.decimo.merchant_service.dto.Location;
import it.decimo.merchant_service.dto.MerchantStatusDto;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.model.MerchantData;
import it.decimo.merchant_service.repository.MerchantDataRepository;
import it.decimo.merchant_service.repository.MerchantRepository;
import it.decimo.merchant_service.util.Distance;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantDataRepository merchantDataRepository;

    /**
     * Controlla se esiste un {@link Merchant} con l'id richiesto
     */
    public boolean merchantExists(int id) {
        return merchantRepository.findById(id).isPresent();
    }

    /**
     * Salva il merchant passato come parametro all'interno del DB
     * 
     * @param merchant il merchant da salvare
     * @return {@code null} se non è stato possibile salvare il merchant, altrimenti
     *         il suo {@code id}
     */
    public Integer saveMerchant(Merchant merchant) {
        try {
            log.info("Saving merchant '{}'", merchant.getStoreName());
            final var merchId = merchantRepository.save(merchant).getId();
            final var data = new MerchantData();
            data.setMerchantId(merchId);
            merchantDataRepository.save(data);
            return merchId;
        } catch (Exception e) {
            log.error("Got error while saving merchant {}", merchant.getStoreName(), e);
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

    /***
     * Aggiorna lo status del {@link Merchant} con i dati passati nell'update
     */
    public MerchantData updateMerchant(MerchantStatusDto update) {
        final var data = merchantDataRepository.findById(update.getId()).get();
        if (update.getFreeSeats() != null) {
            data.setFreeSeats(update.getFreeSeats());
        }
        if (update.getTotalSeats() != null) {
            data.setTotalSeats(update.getTotalSeats());
        }

        return merchantDataRepository.save(data);
    }

}
