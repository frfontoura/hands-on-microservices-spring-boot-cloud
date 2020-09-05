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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductCompositeControllerImpl implements ProductCompositeController {

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
    public ProductAggregate getProduct(final int productId) {
        final Product product = integration.getProduct(productId);
        if (product == null) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        final List<Recommendation> recommendations = integration.getRecommendations(productId);

        final List<Review> reviews = integration.getReviews(productId);

        return productAggregateFactory.create(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }
}
