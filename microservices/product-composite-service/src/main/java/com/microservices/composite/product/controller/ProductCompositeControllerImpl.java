package com.microservices.composite.product.controller;

import com.microservices.api.composite.product.ProductAggregate;
import com.microservices.api.composite.product.ProductCompositeController;
import com.microservices.api.core.product.Product;
import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.review.Review;
import com.microservices.composite.product.factory.ProductAggregateFactory;
import com.microservices.composite.product.integration.ProductCompositeIntegration;
import com.microservices.util.exceptions.NotFoundException;
import com.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeControllerImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration integration;
    private final ProductAggregateFactory productAggregateFactory;

    @Autowired
    public ProductCompositeControllerImpl(final ServiceUtil serviceUtil, final ProductCompositeIntegration integration, final ProductAggregateFactory productAggregateFactory) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
        this.productAggregateFactory = productAggregateFactory;
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

        return productAggregateFactory.create(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteCompositeProduct(final int productId) {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        integration.deleteProduct(productId);
        integration.deleteRecommendations(productId);
        integration.deleteReviews(productId);
        LOG.debug("getCompositeProduct: aggregate entities deleted for productId: {}", productId);
    }
}
