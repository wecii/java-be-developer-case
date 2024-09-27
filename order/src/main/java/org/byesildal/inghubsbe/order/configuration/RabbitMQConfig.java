package org.byesildal.inghubsbe.order.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String BUY_SELL_ROUTING_KEY = "order.buy-sell";
    public static final String CANCEL_ROUTING_KEY = "order.cancel";

    @Bean
    public Queue buySellOrderQueue() {
        return new Queue(BUY_SELL_ROUTING_KEY, true);
    }

    @Bean
    public Queue cancelOrderQueue() {
        return new Queue(CANCEL_ROUTING_KEY, true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding buySellOrderBinding(Queue buySellOrderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(buySellOrderQueue).to(orderExchange).with(BUY_SELL_ROUTING_KEY);
    }

    @Bean
    public Binding cancelOrderBinding(Queue cancelOrderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(cancelOrderQueue).to(orderExchange).with(CANCEL_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
