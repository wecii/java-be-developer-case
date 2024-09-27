package org.byesildal.inghubsbe.wallet.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "transaction.exchange";
    public static final String TRANSACTION_ROUTING_KEY = "transaction";

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_ROUTING_KEY, true);
    }

    @Bean
    public DirectExchange transactionExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding transactionBinding(Queue transactionQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(transactionQueue).to(orderExchange).with(TRANSACTION_ROUTING_KEY);
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

