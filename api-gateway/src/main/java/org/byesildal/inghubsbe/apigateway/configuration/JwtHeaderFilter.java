package org.byesildal.inghubsbe.apigateway.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtHeaderFilter extends AbstractGatewayFilterFactory<JwtHeaderFilter.Config> {

    public static class Config {
        // Configurations if needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token != null) {
                // Add the token to the request headers
                exchange = exchange.mutate()
                        .request(r -> r.header("Authorization", token))
                        .build();
            }
            return chain.filter(exchange);
        };
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }
}