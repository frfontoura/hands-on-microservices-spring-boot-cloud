package com.microservices.core.review.controller;

import com.microservices.api.core.review.Review;
import com.microservices.api.core.review.ReviewController;
import com.microservices.util.exceptions.InvalidInputException;
import com.microservices.util.http.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewControllerImpl implements ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewControllerImpl.class);

    private final ServiceUtil serviceUtil;

    @Override
    public List<Review> getReviews(final int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        final List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/reviews response size: {}", list.size());

        return list;
    }
}
