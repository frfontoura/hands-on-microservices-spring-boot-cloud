package com.microservices.composite.product.controller;

import com.microservices.api.composite.product.*;
import com.microservices.api.core.product.Product;
import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.review.Review;
import com.microservices.composite.product.integration.ProductCompositeIntegration;
import com.microservices.util.exceptions.NotFoundException;
import com.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeControllerImpl(final ServiceUtil serviceUtil, final ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public void createCompositeProduct(final ProductAggregate body) {

        try {

            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());

            final Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            integration.createProduct(product);

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(r -> {
                    final Recommendation recommendation = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    integration.createRecommendation(recommendation);
                });
            }

            if (body.getReviews() != null) {
                body.getReviews().forEach(r -> {
                    final Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    integration.createReview(review);
                });
            }

            LOG.debug("createCompositeProduct: composite entites created for productId: {}", body.getProductId());

        } catch (final RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public ProductAggregate getCompositeProduct(final int productId) {
        LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", productId);

        final Product product = integration.getProduct(productId);
        if (product == null) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        final List<Recommendation> recommendations = integration.getRecommendations(productId);

        final List<Review> reviews = integration.getReviews(productId);

        LOG.debug("getCompositeProduct: aggregate entity found for productId: {}", productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteCompositeProduct(final int productId) {

        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

        integration.deleteProduct(productId);

        integration.deleteRecommendations(productId);

        integration.deleteReviews(productId);

        LOG.debug("getCompositeProduct: aggregate entities deleted for productId: {}", productId);
    }

    private ProductAggregate createProductAggregate(final Product product, final List<Recommendation> recommendations, final List<Review> reviews, final String serviceAddress) {

        // 1. Setup product info
        final int productId = product.getProductId();
        final String name = product.getName();
        final int weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        final List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        final List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        final String productAddress = product.getServiceAddress();
        final String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        final String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        final ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}