package it.decimo.merchant_service.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.geo.Point;

import it.decimo.merchant_service.model.Merchant;
import it.decimo.merchant_service.model.MerchantData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDto {

    public MerchantDto(Merchant merchant, MerchantData data) {
        this.id = merchant.getId();
        this.storeLocation = merchant.getPoint();
        this.storeName = merchant.getStoreName();
        this.owner = merchant.getOwner();
        this.occupancyRate = merchant.getOccupancyRate();
        if (data != null) {
            this.freeSeats = data.getFreeSeats();
            this.totalSeats = data.getTotalSeats();
        }
    }

    public MerchantDto(Merchant merchant) {
        this.id = merchant.getId();
        this.storeLocation = merchant.getPoint();
        this.storeName = merchant.getStoreName();
        this.owner = merchant.getOwner();
        this.freeSeats = merchant.getFreeSeats();
        this.totalSeats = merchant.getTotalSeats();
        this.occupancyRate = merchant.getOccupancyRate();
    }

    private Integer id;

    private Point storeLocation;

    /**
     * Contiene la distanza che viene calcolata al momento della query per ciascun
     * client
     */
    private Double distance;

    private Integer freeSeats;
    private Integer totalSeats;

    private double occupancyRate;

    public Point getPoint() {
        return storeLocation;
    }

    @JsonIgnore
    public Map<String, Double> getStoreLocation() {
        return new HashMap<String, Double>() {
            {
                put("lat", storeLocation.getX());
                put("lng", storeLocation.getY());
            }
        };
    }

    public void setStoreLocation(Location location) {
        this.storeLocation = new Point(location.getX(), location.getY());
    }

    private String storeName;

    private Integer owner;
}
