package br.com.devdojo.docs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;

@Configuration
@EnableSwagger2
public class SweggerConfig {
    @Bean
    public Docket apiDoc(){

        RequestParameterBuilder aParameterBuilder = new RequestParameterBuilder();
        aParameterBuilder.name("Authorization")
        .description("Bearer token")
        .query(q -> q.defaultValue("no-cache, no-store")
        .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
        .in(ParameterType.HEADER).required(true).build();

        List<RequestParameter> aParameters = new ArrayList<>();
        aParameters.add(aParameterBuilder.build());

        return new Docket(DocumentationType.SWAGGER_2).select().
        apis(RequestHandlerSelectors.basePackage("br.com.devdojo.endpoint"))
        .paths(PathSelectors.regex("/v1.*")).build().apiInfo(metaData())
        .globalRequestParameters(aParameters);
    }

    private ApiInfo metaData(){
        return new ApiInfoBuilder().title("Spring Boot Essential")
        .description("The best Spring course of there")
        .version("1.0")
        .contact(new Contact("Carlos", "http://devdojo.com.br", "meuemail@gmail.com"))
        .license("Apache License Version 2.0")
        .licenseUrl("https://www.apache.org./license/LICENSE-2.0")
        .build();
    }
}
