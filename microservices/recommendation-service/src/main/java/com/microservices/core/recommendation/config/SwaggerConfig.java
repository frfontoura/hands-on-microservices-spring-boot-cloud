package com.microservices.core.recommendation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static java.util.Collections.emptyList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
public class SwaggerConfig {

    @Value("${api.common.version}")
    private String apiVersion;

    @Value("${api.common.title}")
    private String apiTitle;

    @Value("${api.common.description}")
    private String apiDescription;

    @Value("${api.common.termsOfServiceUrl}")
    private String apiTermsOfServiceUrl;

    @Value("${api.common.license}")
    private String apiLicense;

    @Value("${api.common.licenseUrl}")
    private String apiLicenseUrl;

    @Value("${api.common.contact.name}")
    private String apiContactName;

    @Value("${api.common.contact.url}")
    private String apiContactUrl;

    @Value("${api.common.contact.email}")
    private String apiContactEmail;

    /**
     * Will exposed on $HOST:$PORT/swagger-ui.html
     *
     * @return
     */
    @Bean
    public Docket apiDocumentation() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(basePackage("com.microservices"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfo(
                        apiTitle,
                        apiDescription,
                        apiVersion,
                        apiTermsOfServiceUrl,
                        new Contact(apiContactName, apiContactUrl, apiContactEmail),
                        apiLicense,
                        apiLicenseUrl,
                        emptyList()
                ));
    }
}
