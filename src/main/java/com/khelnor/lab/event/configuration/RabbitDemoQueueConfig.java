package com.khelnor.lab.event.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitDemoQueueConfig {

    public static final String QUEUE_TEST = "test";
    public static final String TEST_EXCHANGE = "test.exchange";
    public static final String DLQ_EXCHANGE = "dlx.exchange";
    public static final String DLQ_QUEUE_NAME = "dlq.test";
    public static final String TEST_ROUTING_KEY = "test.route";
    public static final String DLQ_ROUTING_KEY = "dlq.route";

    public static final String QUEUE_TEST2 = "test2";
    public static final String TEST2_ROUTING_KEY = "test2.route";
    public static final String QUEUE_RETRY_TEST2 = "retry.test2";
    public static final String RETRY_TEST2_ROUTING_KEY = "retry.test2.route";

    @Bean
    DirectExchange testExchange(){
        return new DirectExchange(TEST_EXCHANGE);
    }

    @Bean
    DirectExchange dlqExchange(){
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue testQueue() {
        return QueueBuilder.durable(QUEUE_TEST).
                deadLetterExchange(DLQ_EXCHANGE).
                deadLetterRoutingKey(DLQ_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue test2Queue() {
        return QueueBuilder.durable(QUEUE_TEST2).
                deadLetterExchange(TEST_EXCHANGE).
                deadLetterRoutingKey(RETRY_TEST2_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue retryTest2Queue() {
        return QueueBuilder.durable(QUEUE_RETRY_TEST2).
                ttl(20000).
                deadLetterExchange(TEST_EXCHANGE).
                deadLetterRoutingKey(TEST2_ROUTING_KEY).
                build();
    }

    @Bean
    public Queue dlqTestQueue() {
        return QueueBuilder.durable(DLQ_QUEUE_NAME).
                build();
    }

    @Bean
    Binding testBinding() {
        return BindingBuilder.bind(testQueue()).to(testExchange()).with(TEST_ROUTING_KEY);
    }


    @Bean
    Binding dlqBinding() {
        return BindingBuilder.bind(dlqTestQueue()).to(dlqExchange()).with(DLQ_ROUTING_KEY);
    }

    @Bean
    Binding test2Binding() {
        return BindingBuilder.bind(test2Queue()).to(testExchange()).with(TEST2_ROUTING_KEY);
    }

    @Bean
    Binding retryTest2Binding() {
        return BindingBuilder.bind(retryTest2Queue()).to(testExchange()).with(RETRY_TEST2_ROUTING_KEY);
    }
}