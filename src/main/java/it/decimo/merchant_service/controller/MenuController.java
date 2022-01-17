package it.decimo.merchant_service.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.decimo.merchant_service.dto.BasicResponse;
import it.decimo.merchant_service.model.MenuCategory;
import it.decimo.merchant_service.model.MenuItem;
import it.decimo.merchant_service.service.MenuService;
import it.decimo.merchant_service.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api/merchant/{id}/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MerchantService merchantService;


    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ritorna la lista di elementi che compongono il menu del locale", content = @Content(array = @ArraySchema(minItems = 0, uniqueItems = true, schema = @Schema(implementation = MenuItem.class)))), @ApiResponse(responseCode = "404", description = "Il ristorante ricercato non esite", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})
    @GetMapping
    public ResponseEntity<Object> getMenu(@PathVariable int id) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @PatchMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ritorna la copia dell'oggetto modificato correttamente", content = @Content(schema = @Schema(implementation = MenuItem.class))),
            @ApiResponse(responseCode = "404", description = "Il ristorante ricercato non esite", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "404", description = "L'oggetto richiesto non esiste", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
            @ApiResponse(responseCode = "401", description = "L'utente che ha richiesto l'update non è autorizzato", content = @Content(schema = @Schema(implementation = BasicResponse.class)))
    })
    public ResponseEntity<Object> updateItem(@PathVariable int id, @RequestBody MenuItem item, @PathParam(value = "requester") int requester) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }
        return menuService.updateItem(id, item, requester);
    }

    @GetMapping("/categories")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ritorna la lista di categorie per i piatti del menu", content = @Content(array = @ArraySchema(minItems = 0, uniqueItems = true, schema = @Schema(implementation = MenuCategory.class))))})
    public List<MenuCategory> getCategories() {
        return menuService.getCategories();
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ritorna l'id dell'oggetto che è stato inserito", content = @Content(schema = @Schema(implementation = BasicResponse.class))), @ApiResponse(responseCode = "404", description = "Il ristorante ricercato non esite", content = @Content(schema = @Schema(implementation = BasicResponse.class))), @ApiResponse(responseCode = "401", description = "L'utente non può effettuare la richiesta perché non è il proprietario del merchant", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})
    @PostMapping
    public ResponseEntity<Object> insertItem(@PathVariable int id, @RequestBody MenuItem item, @PathParam(value = "requester") int requester) {
        if (!merchantService.merchantExists(id)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }
        final var merchant = merchantService.getMerchant(id);
        if (merchant.getOwner() != requester) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final var saved = menuService.save(id, item);

        return ResponseEntity.ok().body(new BasicResponse(Integer.toString(saved.getMenuItemId()), "OK"));
    }

    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ha rimosso l'oggetto dal menu del locale", content = @Content(schema = @Schema(implementation = BasicResponse.class))), @ApiResponse(responseCode = "404", description = "Il locale non è stato trovato", content = @Content(schema = @Schema(implementation = BasicResponse.class))), @ApiResponse(responseCode = "401", description = "L'utente non può effettuare la richiesta perché non è il proprietario del merchant", content = @Content(schema = @Schema(implementation = BasicResponse.class)))})

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteMenuItem(@PathVariable(name = "id") int merchantId, @PathVariable(name = "itemId") int itemId, @PathParam(value = "requester") int requester) {
        if (!merchantService.merchantExists(merchantId)) {
            return ResponseEntity.status(404).body(new BasicResponse("No merchant found", "NO_MERCH_FOUND"));
        }

        final var merchant = merchantService.getMerchant(merchantId);
        if (merchant.getOwner() != requester) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        menuService.deleteItem(itemId, merchantId);
        return ResponseEntity.ok().build();
    }
}
