package com.joelsandvik.repositories;

import com.joelsandvik.models.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}