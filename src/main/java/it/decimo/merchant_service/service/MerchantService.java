package it.decimo.merchant_service.service;

import it.decimo.merchant_service.connectors.PrenotationServiceConnector;
import it.decimo.merchant_service.dto.MerchantDto;
import it.decimo.merchant_service.dto.Prenotation;
import it.decimo.merchant_service.exceptions.NotFoundException;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PrenotationServiceConnector prenotationServiceConnector;

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
    public ResponseEntity<Object> saveMerchant(Merchant merchant) {
        try {
            log.info("Saving merchant '{}'", merchant.getStoreName());
            final var saved = merchantRepository.save(merchant);
            return ResponseEntity.ok(getMerchant(saved.getId()));
        } catch (Exception e) {
            log.error("Got error while saving merchant {}", merchant.getStoreName(), e);
            return ResponseEntity.internalServerError().body(e);
        }
    }

    /**
     * Ritorna la lista di esercenti
     *
     * @return La lista degli esercenti, opzionalmente ordinata (se point è
     * definito)
     */
    public List<MerchantDto> getMerchants() {
        final var merchants = merchantRepository.findAll()
                .stream()
                .map(MerchantDto::new)
                .collect(Collectors.toList());

        log.info("Found {} merchants", merchants.size());

        return merchants;
    }

    /***
     * Aggiorna lo status del {@link Merchant} con i dati passati nell'update
     */
    public Merchant updateMerchant(Merchant update) {
        try {
            final var data = merchantRepository.findById(update.getId()).get();

            update.setId(data.getId());
            log.info("Updating merchant {}", update.getId());
            return merchantRepository.save(update);
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
    public MerchantDto getMerchant(Integer id) throws NotFoundException {
        if (!merchantExists(id)) {
            return null;
        }

        final var found = merchantRepository.findById(id);
        if (found.isEmpty()) {
            throw new NotFoundException("Il merchant richiesto non esiste");
        }

        final var merchant = found.get();

        final var prenotations = prenotationServiceConnector.getPrenotationsOfMerchant(merchant.getId());
        if (prenotations.getStatusCode() == HttpStatus.OK) {
            final var prenotationsToCompute = ((List<Prenotation>) prenotations.getBody());
            log.info("Got {} prenotations for merchant {}", prenotationsToCompute.size(), merchant.getId());
            merchant.setFreeSeats(merchant.getTotalSeats() - prenotationsToCompute.stream().map(Prenotation::getAmount).reduce(0, Integer::sum));
            log.info("Merchant {} has {} free seats", merchant.getId(), merchant.getFreeSeats());
            if (merchant.getTotalSeats() == 0) {
                merchant.setOccupancyRate(100);
            } else {
                merchant.setOccupancyRate((merchant.getFreeSeats() / merchant.getTotalSeats()) * 100);
            }
            log.info("Merchant {} has an occupancy rate of {}", merchant.getId(), merchant.getOccupancyRate());
        }
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
