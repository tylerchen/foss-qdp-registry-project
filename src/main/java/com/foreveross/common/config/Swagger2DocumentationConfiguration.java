/*******************************************************************************
 * Copyright (c) Oct 23, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.config;

import org.iff.infra.util.SocketHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.PropertySourcedMapping;
import springfox.documentation.spring.web.PropertySourcedRequestMappingHandlerMapping;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Swagger2 配置，已经重写了，不需要加@EnableSwagger2。
 */
@ConditionalOnProperty(name = "springfox.documentation.swagger2.enable", havingValue = "true")
@Configuration
@Import({SpringfoxWebMvcConfiguration.class, SwaggerCommonConfiguration.class})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger2.readers.parameter",
        "springfox.documentation.swagger2.mappers"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"springfox.documentation.swagger2.web.*"}),
})
public class Swagger2DocumentationConfiguration {

    @Value("${spring.application.name:UNKOWN-APP-NAME}")
    private String applicationName;
    @Value("${springfox.documentation.swagger2.basePackage:com.foreveross.qdp}")
    private String basePackage;

    @Bean
    public JacksonModuleRegistrar swagger2Module() {
        return new Swagger2JacksonModule();
    }

    @Bean
    public HandlerMapping swagger2ControllerMapping(
            Environment environment,
            DocumentationCache documentationCache,
            ServiceModelToSwagger2Mapper mapper,
            JsonSerializer jsonSerializer) {
        return new PropertySourcedRequestMappingHandlerMapping(
                environment,
                new CustomSwagger2Controller(environment, documentationCache, mapper, jsonSerializer));
    }

    @Bean
    public Docket docketApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName + " API")
                .description(applicationName + " API auto generate by QDP.")
                .contact(new Contact("FOSS", "http://www.foreveross.com", "iffiff1@gmail.com"))
                .version("1.0.0")
                .build();
    }

    @Controller
    @ApiIgnore
    public class CustomSwagger2Controller extends Swagger2Controller {

        @Autowired
        public CustomSwagger2Controller(Environment environment, DocumentationCache documentationCache, ServiceModelToSwagger2Mapper mapper, JsonSerializer jsonSerializer) {
            super(environment, documentationCache, mapper, jsonSerializer);
        }

        @RequestMapping(
                value = "/old" + DEFAULT_URL,
                method = RequestMethod.GET,
                produces = {APPLICATION_JSON_VALUE, "application/hal+json"})
        @ResponseBody
        public ResponseEntity<Json> getDocumentation(String swaggerGroup, HttpServletRequest servletRequest) {
            return super.getDocumentation(swaggerGroup, servletRequest);
        }

        @RequestMapping(
                value = DEFAULT_URL,
                method = RequestMethod.GET,
                produces = {APPLICATION_JSON_VALUE, "application/hal+json"})
        @PropertySourcedMapping(
                value = "${springfox.documentation.swagger.v2.path}",
                propertyKey = "springfox.documentation.swagger.v2.path")
        @ResponseBody
        public void getDocumentation2(
                @RequestParam(value = "group", required = false) String swaggerGroup,
                HttpServletRequest servletRequest, HttpServletResponse response) {
            try {
                ResponseEntity<Json> entity = getDocumentation(swaggerGroup, servletRequest);
                String value = entity.getBody().value();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(value);
                SocketHelper.closeWithoutError(response.getWriter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}