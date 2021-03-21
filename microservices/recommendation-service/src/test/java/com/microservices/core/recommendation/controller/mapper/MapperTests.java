package com.microservices.core.recommendation.controller.mapper;


import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.core.recommendation.domain.entity.RecommendationEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MapperTests {

    private final RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        final Recommendation api = new Recommendation(1, 2, "a", 4, "C", "adr");

        final RecommendationEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getRecommendationId(), entity.getRecommendationId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRate(), entity.getRating());
        assertEquals(api.getContent(), entity.getContent());

        final Recommendation api2 = mapper.entityToApi(entity);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getRecommendationId(), api2.getRecommendationId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getRate(), api2.getRate());
        assertEquals(api.getContent(), api2.getContent());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        final Recommendation api = new Recommendation(1, 2, "a", 4, "C", "adr");
        final List<Recommendation> apiList = Collections.singletonList(api);

        final List<RecommendationEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        final RecommendationEntity entity = entityList.get(0);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getRecommendationId(), entity.getRecommendationId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRate(), entity.getRating());
        assertEquals(api.getContent(), entity.getContent());

        final List<Recommendation> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        final Recommendation api2 = api2List.get(0);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getRecommendationId(), api2.getRecommendationId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getRate(), api2.getRate());
        assertEquals(api.getContent(), api2.getContent());
        assertNull(api2.getServiceAddress());
    }
}