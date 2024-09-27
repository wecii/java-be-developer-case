package org.byesildal.inghubsbe.apigateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter  extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${auth-service-uri}")
    private String authServiceUri;

    private final WebClient webClient = WebClient.builder().build();

    public JwtAuthenticationFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(new RuntimeException("Missing or invalid Authorization header"));
            }

            return webClient.method(HttpMethod.POST)
                    .uri(authServiceUri)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .then(Mono.defer(() -> chain.filter(exchange)))
                    .onErrorResume(e -> Mono.error(new RuntimeException("Invalid token: " + e.getMessage())));
        };
    }

    public static class Config {
    }
}