package it.decimo.merchant_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.repository.MerchantRepository;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping(produces = "application/json")
    public ResponseEntity<Object> findAll() {

        final var merchants = merchantRepository.findAll();

        return ResponseEntity.ok().body(merchants);
    }

    @PostMapping(value = "/", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Il merchant è stato salvato", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })
    public ResponseEntity<Object> saveItem(@RequestBody Merchant merchant) {
        final var item = merchantRepository.save(merchant);

        return ResponseEntity.ok().body(BasicResponse.builder().message(item.getId().toString()));
    }
}
