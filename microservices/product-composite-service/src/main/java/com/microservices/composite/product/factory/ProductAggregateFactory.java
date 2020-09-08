package com.microservices.composite.product.factory;

import com.microservices.api.composite.product.ProductAggregate;
import com.microservices.api.composite.product.RecommendationSummary;
import com.microservices.api.composite.product.ReviewSummary;
import com.microservices.api.composite.product.ServiceAddresses;
import com.microservices.api.core.product.Product;
import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.review.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductAggregateFactory {

    public ProductAggregate create(final Product product, final List<Recommendation> recommendations, final List<Review> reviews, final String serviceAddress) {

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
