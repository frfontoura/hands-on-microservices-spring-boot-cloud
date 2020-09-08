package com.microservices.core.review.controller;

import com.microservices.api.core.review.Review;
import com.microservices.api.core.review.ReviewController;
import com.microservices.core.review.controller.mapper.ReviewMapper;
import com.microservices.core.review.domain.entity.ReviewEntity;
import com.microservices.core.review.domain.repository.ReviewRepository;
import com.microservices.util.exceptions.InvalidInputException;
import com.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewControllerImpl implements ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewControllerImpl.class);

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;

    @Autowired
    public ReviewControllerImpl(final ReviewRepository repository, final ReviewMapper mapper, final ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Review createReview(final Review body) {
        try {
            final ReviewEntity entity = mapper.apiToEntity(body);
            final ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(newEntity);

        } catch (final DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public List<Review> getReviews(final int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        final List<ReviewEntity> entityList = repository.findByProductId(productId);
        final List<Review> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getReviews: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteReviews(final int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
