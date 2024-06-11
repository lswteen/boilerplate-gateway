package com.renzo.core.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "boilerplate.gateway")

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteProperties {
    Map<String, List<RouteDefinition>> routes;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class RouteDefinition{
        List<FilterDefinition> filters;
        List<PredicateDefinition> predicates;
    }
}
