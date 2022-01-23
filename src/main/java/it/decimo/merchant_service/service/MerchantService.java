package it.decimo.merchant_service.service;

import it.decimo.merchant_service.dto.Location;
import it.decimo.merchant_service.dto.MerchantDto;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.repository.CustomRepository;
import it.decimo.merchant_service.repository.MerchantRepository;
import it.decimo.merchant_service.util.Distance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MerchantService {
    @Autowired
    private CustomRepository customRepository;
    @Autowired
    private MerchantRepository merchantRepository;


    /**
     * Controlla se esiste un {@link Merchant} con l'id richiesto
     */
    public boolean merchantExists(Integer id) {
        log.info("Looking for existance of if {}", id);
        final var found = merchantRepository.findById(id);
        log.info("Merchant of id {} exists: {}", id, found.isPresent());

        if (found.isPresent()) {
            final var merch = found.get();
            log.info("Requested merchant {} is found", id);
            if (!merch.isEnabled()) {
                log.info("Merchant is deleted");
            }
            return merch.isEnabled();
        }
        return false;
    }

    /**
     * Controlla se esiste un {@link Merchant} con l'id richiesto e se l'utente passato è l'owner
     */
    public boolean isUserOwner(int userId, int merchantId) {
        final var found = merchantRepository.findById(merchantId);

        if (found.isEmpty()) {
            log.info("Merchant {} not found", merchantId);
            return false;
        }

        final var merch = found.get();

        log.info("Found merchant {} of owner {}", merchantId, merch.getOwner());

        if (merch.getOwner() != userId) {
            log.info("User {} is not the owner of merchant {}", userId, merchantId);
            return false;
        } else {
            log.info("User {} is the owner of merchant {}", userId, merchantId);
            return true;
        }
    }

    /**
     * Salva il merchant passato come parametro all'interno del DB
     *
     * @param merchant il merchant da salvare
     * @return {@code null} se non è stato possibile salvare il merchant, altrimenti
     * il suo {@code id}
     */
    public MerchantDto saveMerchant(Merchant merchant) {
        try {
            log.info("Saving merchant '{}'", merchant.getStoreName());
            final var saved = merchantRepository.save(merchant);
            return getMerchant(saved.getId());
        } catch (Exception e) {
            log.error("Got error while saving merchant {}", merchant.getStoreName(), e);
            return null;
        }
    }

    /**
     * Ritorna la lista di esercenti
     *
     * @param point Opzionale -- definisce il centro dal quale recuperare i
     *              merchant. Se definito i merchant vengono ritornati ordinati
     * @return La lista degli esercenti, opzionalmente ordinata (se point è
     * definito)
     */
    public List<MerchantDto> getMerchants(Location point) {
        final var merchants = customRepository.findAllMerchantsWithMetadata();

        log.info("Found {} merchants", merchants.size());
        final var toReturn = new ArrayList<MerchantDto>();

        merchants.forEach(m -> {
            toReturn.add(new MerchantDto(m));
        });

        if (point != null && (point.getX() != null && point.getY() != null)) {

            for (MerchantDto merchant : toReturn) {
                final var merchantPosition = merchant.getPoint();
                if (merchantPosition != null) {
                    final var distance = Distance.gps2m(merchantPosition, point.toPoint());
                    merchant.setDistance(distance);
                }

            }
            toReturn.sort((dto1, dto2) -> {
                if (dto1 == null || dto2 == null || dto1.getDistance() == null || dto2.getDistance() == null) {
                    return 0;
                }
                return dto1.getDistance().compareTo(dto2.getDistance());
            });
        }

        return toReturn;
    }

    /***
     * Aggiorna lo status del {@link Merchant} con i dati passati nell'update
     */
    public Merchant updateMerchant(Merchant update) {
        try {
            final var data = merchantRepository.findById(update.getId()).get();

            update.setId(data.getId());

            return merchantRepository.save(data);
        } catch (Exception e) {
            log.warn("Failed to update merchant {}", update.getId(), e);
            return null;
        }
    }

    /**
     * Ritorna il {@link Merchant} con l'id richiesto
     *
     * @param id L'id del merchant
     * @return Il merchant con l'id richiesto, oppure {@code null} se non esiste
     */
    public MerchantDto getMerchant(Integer id) {
        if (!merchantExists(id)) {
            return null;
        }
        final var merchant = customRepository.getMerchantData(id);
        return new MerchantDto(merchant);
    }

    /**
     * Elimina il merchant con l'id richiesto (eliminazione logica)
     *
     * @param merchantId Il merchant da eliminare
     * @throws IllegalArgumentException se non è stato trovato il merchant richiesto oppure l'utente non è il proprietario del locale e non può eliminarlo
     */
    public void deleteMerchant(int merchantId, int requesterId) throws IllegalArgumentException {
        if (!merchantExists(merchantId)) {
            throw new IllegalArgumentException("Merchant with id " + merchantId + " does not exist");
        }

        final var merchant = merchantRepository.findById(merchantId).get();
        if (merchant.getOwner() == requesterId) {
            merchant.setEnabled(false);
            merchantRepository.save(merchant);
        } else {
            throw new IllegalArgumentException("Merchant with id " + merchantId + " is not owned by user " + requesterId);
        }
    }
}
