package it.decimo.merchant_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.service.MerchantService;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

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
}
