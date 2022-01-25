package it.decimo.merchant_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "category")
@Getter
@Setter
public class MenuCategory {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name")
    private String name;
}
