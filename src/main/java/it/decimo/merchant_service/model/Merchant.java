package it.decimo.merchant_service.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.geo.Point;

import it.decimo.merchant_service.dto.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "merchant")
public class Merchant {

    @GeneratedValue
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "store_location")
    @JsonAlias(value = "store_location")
    private Point storeLocation;

    /**
     * Contiene la distanza che viene calcolata al momento della query per ciascun
     * client
     */
    @JsonAnyGetter
    private Double distance;

    @JsonIgnore
    public Point getPoint() {
        return storeLocation;
    }

    @JsonAnyGetter
    public Map<String, Double> getStoreLocation() {
        return new HashMap<String, Double>() {
            {
                put("lat", storeLocation.getX());
                put("lng", storeLocation.getY());
            }
        };
    }

    @JsonAnySetter
    public void setStoreLocation(Location location) {
        this.storeLocation = new Point(location.getX(), location.getY());
    }

    @Column(name = "store_name")
    @JsonAlias(value = "store_name")
    private String storeName;

    @JsonAlias(value = "user_owner")
    @Column(name = "user_owner")
    private Integer userOwner;

}
