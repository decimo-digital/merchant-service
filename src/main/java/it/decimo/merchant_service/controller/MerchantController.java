package it.decimo.merchant_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.dto.Location;
import it.decimo.merchant_service.dto.MerchantDto;
import it.decimo.merchant_service.dto.MerchantStatusDto;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.model.MerchantData;
import it.decimo.merchant_service.repository.MerchantDataRepository;
import it.decimo.merchant_service.repository.MerchantRepository;
import it.decimo.merchant_service.service.MerchantService;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
    @Autowired
    private MerchantDataRepository merchantDataRepository;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping(produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ritorna la lista di esercenti disponibili. Opzionalmente ordinata", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Merchant.class), minItems = 0, uniqueItems = true))) })
    public ResponseEntity<Object> findAll(@RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lng", required = false) Double lng) {
        final var merchants = merchantService.getMerchants(new Location(lat, lng));
        return ResponseEntity.ok().body(merchants);
    }

    @PostMapping(value = "/", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato salvato", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "500", description = "Per qualche problema non ha salvato il merchant", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })
    public ResponseEntity<Object> saveItem(@RequestBody Merchant merchant) {
        final var id = merchantService.saveMerchant(merchant);
        if (id == null) {
            return ResponseEntity.internalServerError()
                    .body(new BasicResponse("C'è stato qualche errore a salvare il merchant", "GENERIC_ERROR"));
        } else {
            return ResponseEntity.ok().body(BasicResponse.builder().message(id.toString()).code("OK").build());
        }
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato aggiornato", content = @Content(schema = @Schema(implementation = MerchantData.class))),
            @ApiResponse(responseCode = "404", description = "Il merchant richiesto non esiste") })
    public ResponseEntity<Object> patchMerchantStatus(@PathVariable int id, @RequestBody MerchantStatusDto update) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.notFound().build();
        }
        update.setId(id);
        final var newData = merchantService.updateMerchant(update);
        return ResponseEntity.ok().body(newData);
    }

    @GetMapping("/{id}/data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "I dati del merchant richiesto", content = @Content(schema = @Schema(implementation = MerchantDto.class))),
            @ApiResponse(responseCode = "404", description = "Il merchant richiesto non esiste") })
    public ResponseEntity<Object> getMerchantData(@PathVariable int id) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.notFound().build();
        }

        final var merchantData = merchantDataRepository.findById(id).get();

        final var merchant = merchantRepository.getById(id);

        final var merchantDto = new MerchantDto(merchant, merchantData);

        return ResponseEntity.ok(merchantDto);
    }
}
