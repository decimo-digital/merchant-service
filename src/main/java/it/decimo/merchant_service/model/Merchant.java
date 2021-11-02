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

import org.springframework.data.geo.Point;

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
    // @JsonDeserialize(using = PointDeserializer.class)
    // @JsonSerialize(using = PointSerializer.class)
    private Point storeLocation;

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
    public void setStoreLocation(StoreLocation location) {
        this.storeLocation = new Point(location.getX(), location.getY());
    }

    @Column(name = "store_name")
    @JsonAlias(value = "store_name")
    private String storeName;

    @JsonAlias(value = "user_owner")
    @Column(name = "user_owner")
    private Integer userOwner;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class StoreLocation {
    private double x;
    private double y;

    public StoreLocation(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

}