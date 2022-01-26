package it.decimo.merchant_service.dto;

import it.decimo.merchant_service.model.Merchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDto {

    private Integer id;
    private String description;
    private Integer freeSeats;
    private Integer totalSeats;
    private double occupancyRate;
    private String storeName;
    private Integer owner;
    private String cuisineType;


    public MerchantDto(Merchant merchant) {
        this.id = merchant.getId();
        this.storeName = merchant.getStoreName();
        this.owner = merchant.getOwner();
        this.description = merchant.getDescription();
        this.freeSeats = merchant.getFreeSeats();
        this.cuisineType = merchant.getCuisineType();
        this.totalSeats = merchant.getTotalSeats();
        this.occupancyRate = merchant.getOccupancyRate();
    }

}
