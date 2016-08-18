package com.joelsandvik;

import com.github.javafaker.Faker;
import com.joelsandvik.models.Product;
import com.joelsandvik.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@Configuration
@Profile("dev")
public class DevConfig {

    private final Faker faker = new Faker();

    @Bean
    public CommandLineRunner initializeDb(ProductRepository repository) {
        return (args) -> {
            repository.deleteAll();
            Random r = new Random(System.currentTimeMillis());
            List<Currency> allCcs = new ArrayList<>(Currency.getAvailableCurrencies());

            for (int i = 0; i < 20; i++) {

                Map<Currency, Long> ccys = new HashMap<>();
                for (int j = 0; j < r.nextInt(10); j++) {
                    ccys.put(allCcs.get(r.nextInt(allCcs.size())), Long.valueOf(r.nextInt(20000) + 100));
                }

                repository.save(new Product(null,
                        faker.lorem().word(),
                        faker.lorem().sentence(),
                        faker.lorem().words(),
                        ccys
                ));

            }
        };
    }

}
