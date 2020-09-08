package com.microservices.core.recommendation.controller;

import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.recommendation.RecommendationController;
import com.microservices.core.recommendation.controller.mapper.RecommendationMapper;
import com.microservices.core.recommendation.domain.entity.RecommendationEntity;
import com.microservices.core.recommendation.domain.repository.RecommendationRepository;
import com.microservices.util.exceptions.InvalidInputException;
import com.microservices.util.http.ServiceUtil;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendationControllerImpl implements RecommendationController {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationControllerImpl.class);

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationControllerImpl(final RecommendationRepository repository, final RecommendationMapper mapper, final ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Recommendation createRecommendation(final Recommendation body) {
        try {
            final RecommendationEntity entity = mapper.apiToEntity(body);
            final RecommendationEntity newEntity = repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return mapper.entityToApi(newEntity);

        } catch (final DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public List<Recommendation> getRecommendations(final int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        final List<RecommendationEntity> entityList = repository.findByProductId(productId);
        final List<Recommendation> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRecommendations: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecommendations(final int productId) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
