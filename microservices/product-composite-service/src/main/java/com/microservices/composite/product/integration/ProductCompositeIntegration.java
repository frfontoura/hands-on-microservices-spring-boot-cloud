package com.microservices.composite.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.api.core.product.Product;
import com.microservices.api.core.product.ProductController;
import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.recommendation.RecommendationController;
import com.microservices.api.core.review.Review;
import com.microservices.api.core.review.ReviewController;
import com.microservices.util.exceptions.InvalidInputException;
import com.microservices.util.exceptions.NotFoundException;
import com.microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductController, RecommendationController, ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(
            final RestTemplate restTemplate,
            final ObjectMapper mapper,

            @Value("${app.product-service.host}") final String productServiceHost,
            @Value("${app.product-service.port}") final int productServicePort,

            @Value("${app.recommendation-service.host}") final String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") final int recommendationServicePort,

            @Value("${app.review-service.host}") final String reviewServiceHost,
            @Value("${app.review-service.port}") final int reviewServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
    }

    @Override
    public Product createProduct(final Product body) {

        try {
            final String url = productServiceUrl;
            LOG.debug("Will post a new product to URL: {}", url);

            final Product product = restTemplate.postForObject(url, body, Product.class);
            LOG.debug("Created a product with id: {}", product.getProductId());

            return product;

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Product getProduct(final int productId) {

        try {
            final String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the getProduct API on URL: {}", url);

            final Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(final int productId) {
        try {
            final String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the deleteProduct API on URL: {}", url);

            restTemplate.delete(url);

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Recommendation createRecommendation(final Recommendation body) {

        try {
            final String url = recommendationServiceUrl;
            LOG.debug("Will post a new recommendation to URL: {}", url);

            final Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(final int productId) {

        try {
            final String url = recommendationServiceUrl + "?productId=" + productId;

            LOG.debug("Will call the getRecommendations API on URL: {}", url);
            final List<Recommendation> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
            }).getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        } catch (final Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRecommendations(final int productId) {
        try {
            final String url = recommendationServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Review createReview(final Review body) {

        try {
            final String url = reviewServiceUrl;
            LOG.debug("Will post a new review to URL: {}", url);

            final Review review = restTemplate.postForObject(url, body, Review.class);
            LOG.debug("Created a review with id: {}", review.getProductId());

            return review;

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Review> getReviews(final int productId) {

        try {
            final String url = reviewServiceUrl + "?productId=" + productId;

            LOG.debug("Will call the getReviews API on URL: {}", url);
            final List<Review> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {
            }).getBody();

            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        } catch (final Exception ex) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteReviews(final int productId) {
        try {
            final String url = reviewServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);

        } catch (final HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(final HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(final HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (final IOException ioex) {
            return ioex.getMessage();
        }
    }
}
