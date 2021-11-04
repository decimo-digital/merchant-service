package it.decimo.merchant_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.service.MenuService;

@RestController
@RequestMapping("/api/merchant/{id}/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ritorna la lista di elementi che compongono il menu del locale", content = @Content(array = @ArraySchema(minItems = 0, uniqueItems = true, schema = @Schema(implementation = MenuItem.class)))),
            @ApiResponse(responseCode = "404", description = "Il ristorante ricercato non esite", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })
    @GetMapping
    public ResponseEntity<Object> getMenu(@PathVariable int id) {
        if (!menuService.doesMerchantExists(id)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ritorna l'id dell'oggetto che è stato inserito", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "Il ristorante ricercato non esite", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })
    @PostMapping
    public ResponseEntity<Object> insertItem(@PathVariable int id, @RequestBody MenuItem item) {
        if (!menuService.doesMerchantExists(id)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }

        final var saved = menuService.save(id, item);

        return ResponseEntity.ok().body(new BasicResponse(Integer.toString(saved.getMenuItemId()), "OK"));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ha rimosso l'oggetto dal menu del locale", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "Il locale non è stato trovato", content = @Content(schema = @Schema(implementation = BasicResponse.class))) })

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteMenuItem(@PathVariable(name = "id") int merchantId,
            @PathVariable(name = "itemId") int itemId) {
        if (!menuService.doesMerchantExists(merchantId)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }

        menuService.deleteItem(itemId, merchantId);
        return ResponseEntity.ok().build();
    }
}
