package it.decimo.merchant_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "merchant")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "store_name", nullable = false)
    @JsonAlias(value = "store_name")
    private String storeName;

    @JsonAlias(value = "owner")
    @Column(name = "owner", nullable = false)
    private Integer owner;

    @Transient
    @JsonAlias(value = "free_seats")
    private Integer freeSeats;

    @Column(name = "description")
    private String description;

    @Transient
    @JsonAlias(value = "occupancy_rate")
    private float occupancyRate;

    @Column(name = "total_seats", nullable = false)
    @JsonAlias(value = "total_seats")
    private Integer totalSeats;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "image", length = 10485760)
    private String image;
}
