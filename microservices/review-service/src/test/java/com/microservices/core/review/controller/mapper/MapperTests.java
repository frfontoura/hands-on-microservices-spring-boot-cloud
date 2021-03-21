package com.microservices.core.review.controller.mapper;

import com.microservices.api.core.review.Review;
import com.microservices.core.review.domain.entity.ReviewEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTests {

    private final ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        final Review api = new Review(1, 2, "a", "s", "C", "adr");

        final ReviewEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getReviewId(), entity.getReviewId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getSubject(), entity.getSubject());
        assertEquals(api.getContent(), entity.getContent());

        final Review api2 = mapper.entityToApi(entity);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getReviewId(), api2.getReviewId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getSubject(), api2.getSubject());
        assertEquals(api.getContent(), api2.getContent());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        final Review api = new Review(1, 2, "a", "s", "C", "adr");
        final List<Review> apiList = Collections.singletonList(api);

        final List<ReviewEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        final ReviewEntity entity = entityList.get(0);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getReviewId(), entity.getReviewId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getSubject(), entity.getSubject());
        assertEquals(api.getContent(), entity.getContent());

        final List<Review> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        final Review api2 = api2List.get(0);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getReviewId(), api2.getReviewId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getSubject(), api2.getSubject());
        assertEquals(api.getContent(), api2.getContent());
        assertNull(api2.getServiceAddress());
    }
}