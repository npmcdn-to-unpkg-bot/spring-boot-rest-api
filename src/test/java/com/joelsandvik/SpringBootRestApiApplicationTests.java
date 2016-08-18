package com.joelsandvik;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joelsandvik.models.Product;
import com.joelsandvik.repositories.ProductRepository;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringBootRestApiApplicationTests {

    @Autowired
    TestRestTemplate template;

    @Autowired
    ProductRepository repository;

    @Autowired
    @Qualifier("productA")
    Product productA;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGetAll() throws IOException, URISyntaxException {
        Traverson.TraversalBuilder tb = new Traverson(new URI("http://localhost:8080/api"), MediaTypes.HAL_JSON).follow("products");
        ParameterizedTypeReference<Resources<Product>> typeRefDevices
                = new ParameterizedTypeReference<Resources<Product>>() {};
        Resources<Product> resProducts = tb.toObject(typeRefDevices);
        Collection<Product> products = resProducts.getContent();
        Assert.assertEquals(3, products.size());
    }

    @Test
    public void testGetProductWithId() {
        ResponseEntity<Product> resp = template.getForEntity("/api/products/" + productA.getProductId(), Product.class);
        Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assert.assertEquals(productA.getProductId(), resp.getBody().getProductId());
    }

    @Test
    public void testGetProductWithNonExistentId() {
        ResponseEntity<Product> resp = template.getForEntity("/api/products/-1", Product.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    public void testAddNewProduct() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Product p = new Product(null,
                "p",
                "description for p",
                new HashSet<>(),
                new HashMap<>()
        );

        ResponseEntity<Product> resp = template.postForEntity("/api/products/", p, Product.class);
        Assert.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        Assert.assertEquals(p.getName(), repository.findOne(resp.getBody().getProductId()).getName());
        repository.delete(resp.getBody().getProductId());
    }

    @Test
    public void testAddExistingProduct() {
        ResponseEntity<Product> resp = template.postForEntity("/api/products/", productA, Product.class);
        Assert.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        Product post = repository.findOne(resp.getBody().getProductId());
        Assert.assertEquals(productA.getName(), post.getName());
    }

    @Test
    public void testUpdateProduct() {
        Product p = new Product(productA.getProductId(),
                "a new name",
                productA.getDescription(),
                Arrays.asList("new", "tags"),
                productA.getPricePoints()
        );

        HttpEntity<Product> httpEntity = new HttpEntity<>(p);
        ResponseEntity<Product> resp = template.exchange("http://localhost:8080/api/products/" + p.getProductId(),
                HttpMethod.PUT, httpEntity, Product.class);
        Product post = repository.findOne(productA.getProductId());
        Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assert.assertEquals(p.getName(), resp.getBody().getName());
        Assert.assertEquals(p.getName(), post.getName());
    }

    @Test
    public void testUpdateNonExistentProduct() {
        Product p = new Product(Long.valueOf(74123),
                "a new name",
                "a description",
                Arrays.asList("some", "tags"),
                Collections.emptyMap()
        );
        HttpEntity<Product> httpEntity = new HttpEntity<>(p);
        ResponseEntity<Product> resp = template.exchange("http://localhost:8080/api/products/" + p.getProductId(),
                HttpMethod.PUT, httpEntity, Product.class);
        Assert.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        Product post = repository.findOne(resp.getBody().getProductId());
        Assert.assertEquals(p.getName(), post.getName());
        repository.delete(post);
    }

    @Test
    public void testSetPricePointOnProduct() {

        Map<Currency, Long> pricePoints = productA.getPricePoints();
        pricePoints.put(Currency.getInstance("GBP"), Long.valueOf(23122));
        Product p = new Product(productA.getProductId(),
                productA.getName(),
                productA.getDescription(),
                productA.getTags(),
                pricePoints
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        RestTemplate restTemplate = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        ResponseEntity<Product> resp = restTemplate.exchange("http://localhost:8080/api/products/" + p.getProductId(),
                HttpMethod.PATCH, new HttpEntity<Product>(productA),
                Product.class);
        Assert.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assert.assertEquals(p.getPricePoints().get(Currency.getInstance("GBP")),
                resp.getBody().getPricePoints().get(Currency.getInstance("GBP")));
        Product post = repository.findOne(p.getProductId());
        Assert.assertEquals(p.getPricePoints().get(Currency.getInstance("GBP")),
                post.getPricePoints().get(Currency.getInstance("GBP")));
    }

}
