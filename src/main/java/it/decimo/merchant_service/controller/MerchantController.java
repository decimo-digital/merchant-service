package it.decimo.merchant_service.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.dto.MerchantDto;
import it.decimo.merchant_service.exceptions.NotFoundException;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.service.MerchantService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Slf4j
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping(produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ritorna la lista di esercenti disponibili. Opzionalmente ordinata", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Merchant.class), minItems = 0, uniqueItems = true)))})
    public ResponseEntity<Object> findAll() {
        final var merchants = merchantService.getMerchants();
        return ResponseEntity.ok().body(merchants);
    }

    @PostMapping(produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato salvato", content = @Content(schema = @Schema(implementation = MerchantDto.class))),
            @ApiResponse(responseCode = "500", description = "Per qualche problema non ha salvato il merchant", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})
    public ResponseEntity<Object> saveItem(@RequestBody Merchant merchant) {
        final var merch = merchantService.saveMerchant(merchant);
        if (merch == null) {
            return ResponseEntity.internalServerError()
                    .body(new BasicResponse("C'è stato qualche errore a salvare il merchant", "GENERIC_ERROR"));
        } else {
            return ResponseEntity.ok().body(merch);
        }
    }

    @PostMapping("/{id}/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato aggiornato", content = @Content(schema = @Schema(implementation = Merchant.class))),
            @ApiResponse(responseCode = "404", description = "Il merchant richiesto non esiste")})
    public ResponseEntity<Object> patchMerchantStatus(@PathVariable int id, @RequestBody Merchant update) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.notFound().build();
        }

        final var newMerchant = merchantService.updateMerchant(update);
        return ResponseEntity.ok().body(newMerchant);
    }

    @GetMapping("/{id}/data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "I dati del merchant richiesto", content = @Content(schema = @Schema(implementation = MerchantDto.class))),
            @ApiResponse(responseCode = "404", description = "Il merchant richiesto non esiste")})
    public ResponseEntity<Object> getMerchantData(@PathVariable int id) {
        try {
            log.info("Getting data of merchant {}", id);
            final var merchantDto = merchantService.getMerchant(id);
            if (merchantDto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(merchantDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(new BasicResponse("Il merchant richiesto non esiste", "MERCHANT_NOT_FOUND"));
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato cancellato", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "Il merchant richiesto non esiste")})
    public ResponseEntity<Object> deleteMerchant(@PathVariable int id, @PathParam("requesterId") Integer requesterId) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.notFound().build();
        }
        merchantService.deleteMerchant(id, requesterId);
        return ResponseEntity.ok().body(new BasicResponse("Merchant cancellato", "MERCHANT_DELETED"));
    }
}
