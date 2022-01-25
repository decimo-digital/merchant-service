package it.decimo.merchant_service.connectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PrenotationServiceConnector {
    private final String path = "/api/prenotation";
    @Value("${app.connectors.prenotationServiceBaseUrl}")
    private String baseUrl;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Ritorna la lista di prenotazioni valide effettuate presso il merchant specificato
     *
     * @return una ResponseEntity. Se è in 200 ritorna una List<Prenotation>, altrimenti il body
     * di risposta come stringa
     */
    public ResponseEntity<Object> getPrenotationsOfMerchant(int merchantId) {
        log.info("Requesting prenotations of merchant {}", merchantId);
        try {
            final String url = baseUrl + path + "/" + merchantId + "/prenotations";
            return restTemplate.getForEntity(url, Object.class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to get prenotations of merchant {}", merchantId, e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
