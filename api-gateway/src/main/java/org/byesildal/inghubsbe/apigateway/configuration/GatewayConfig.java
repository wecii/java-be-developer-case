package org.byesildal.inghubsbe.apigateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${customer-service-uri}")
    private String customerServiceUri;

    @Value("${wallet-service-uri}")
    private String walletServiceUri;

    @Value("${order-service-uri}")
    private String orderServiceUri;

    @Value("${transaction-service-uri}")
    private String transactionServiceUri;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtHeaderFilter jwtHeaderFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtHeaderFilter jwtHeaderFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtHeaderFilter = jwtHeaderFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customer-service", r -> r.path("/api/v1/customer/**")
                        .filters(f -> f.filters(
                                jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()),
                                jwtHeaderFilter.apply(new JwtHeaderFilter.Config())
                        ))
                        .uri(customerServiceUri))

                .route("wallet-service", r -> r.path("/api/v1/wallet/**")
                        .filters(f -> f.filters(
                                jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()),
                                jwtHeaderFilter.apply(new JwtHeaderFilter.Config())
                        ))
                        .uri(walletServiceUri))

                .route("order-service", r -> r.path("/api/v1/order/**")
                        .filters(f -> f.filters(
                                jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()),
                                jwtHeaderFilter.apply(new JwtHeaderFilter.Config())
                        ))
                        .uri(orderServiceUri))
                .route("transaction-service", r -> r.path("/api/v1/transaction/**")
                        .filters(f -> f.filters(
                                jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()),
                                jwtHeaderFilter.apply(new JwtHeaderFilter.Config())
                        ))
                        .uri(transactionServiceUri))
                .build();
    }
}
