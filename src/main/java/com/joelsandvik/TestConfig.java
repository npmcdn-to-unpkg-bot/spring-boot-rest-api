package com.joelsandvik;

import com.github.javafaker.Faker;
import com.joelsandvik.models.Product;
import com.joelsandvik.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@Configuration
@Profile("test")
public class TestConfig {

    private final Faker faker = new Faker();
    private Random r = new Random(System.currentTimeMillis());
    private List<Currency> allCcs = new ArrayList<>(Currency.getAvailableCurrencies());

    @Bean
    Map<Currency, Long> pricePointsA() {
        HashMap<Currency, Long> m = new HashMap<>();
        m.put(allCcs.get(r.nextInt(allCcs.size())), Long.valueOf(r.nextInt(50000000) + 100));
        return m;
    }

    @Bean
    Map<Currency, Long> pricePointsC() {
        HashMap<Currency, Long> m = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            m.put(allCcs.get(r.nextInt(allCcs.size())), Long.valueOf(r.nextInt(500000000) + 100));
        }
        return m;
    }

    @Bean
    @Qualifier("productA")
    Product productA() {
        return new Product(
                null,
                faker.lorem().word(),
                faker.lorem().sentence(),
                null,
                pricePointsA()
        );
    }

    @Bean
    @Qualifier("productB")
    Product productB() {
        return new Product(
                null,
                faker.lorem().word(),
                faker.lorem().sentence(),
                faker.lorem().words(),
                new HashMap<>()
        );
    }

    @Bean
    @Qualifier("productC")
    Product productC() {
        return new Product(
                null,
                faker.lorem().word(),
                faker.lorem().sentence(),
                faker.lorem().words(),
                pricePointsC()
        );
    }


    @Bean
    Collection<Product> products() {

        return new HashSet<>(Arrays.asList(productA(), productB(), productC()));

    }

    @Bean
    public CommandLineRunner initializeDb(ProductRepository repository) {
        return (args) -> {
            repository.deleteAll();
            products().forEach(repository::save);
        };
    }

}
