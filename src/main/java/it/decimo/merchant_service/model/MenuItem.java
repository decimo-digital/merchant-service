package it.decimo.merchant_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "merchant_menu")
@Data
public class MenuItem {
    @Id
    @Column(name = "item_id")
    @GeneratedValue
    private Integer menuItemId;

    @Column(name = "merchant_id")
    private Integer merchantId;

    @Column(name = "category_id")
    @JsonAlias(value = "category_id")
    private Integer categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Float price;
}
