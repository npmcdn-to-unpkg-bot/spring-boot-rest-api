package com.joelsandvik.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long productId;
    private String name;
    private String description;
    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<String> tags;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Currency, Long> pricePoints;

    public Product(Long productId, String name, String description, Collection<String> tags, Map<Currency, Long> pricePoints) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.pricePoints = pricePoints;
    }

    public Product() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Map<Currency, Long> getPricePoints() {
        return pricePoints;
    }

    public void setPricePoints(Map<Currency, Long> pricePoints) {
        this.pricePoints = pricePoints;
    }

}
