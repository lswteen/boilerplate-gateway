package com.renzo;


import com.renzo.route.AuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/api/**")
						.filters(f -> f.filter(new AuthenticationFilter())
								.addRequestHeader("Example", "Header")
								.modifyRequestBody(String.class, String.class,
										(exchange, s) -> Mono.just(s.toUpperCase()))
								.modifyResponseBody(String.class, String.class,
										(exchange, s) -> Mono.just(s.toLowerCase())))
						.uri("http://backend-service"))
				.build();
	}
}
