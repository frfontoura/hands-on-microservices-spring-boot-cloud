package com.microservices.core.product.controller;

import com.microservices.api.core.product.Product;
import com.microservices.api.core.product.ProductController;
import com.microservices.core.product.controller.mapper.ProductMapper;
import com.microservices.core.product.domain.entity.ProductEntity;
import com.microservices.core.product.domain.repository.ProductRepository;
import com.microservices.util.exceptions.InvalidInputException;
import com.microservices.util.exceptions.NotFoundException;
import com.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductControllerImpl implements ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductControllerImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductControllerImpl(final ServiceUtil serviceUtil, final ProductRepository repository, final ProductMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product createProduct(final Product body) {
        try {
            final ProductEntity entity = mapper.apiToEntity(body);
            final ProductEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for productId: {}", body.getProductId());

            return mapper.entityToApi(newEntity);
        } catch (final DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
        }
    }

    @Override
    public Product getProduct(final int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        final ProductEntity entity = repository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

        final Product response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    @Override
    public void deleteProduct(final int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}
