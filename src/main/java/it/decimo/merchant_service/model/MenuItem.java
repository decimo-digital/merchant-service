package it.decimo.merchant_service.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "menu_item")
@Getter
@Setter
public class MenuItem {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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
