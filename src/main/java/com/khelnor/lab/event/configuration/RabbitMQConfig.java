package com.khelnor.lab.event.configuration;

import com.khelnor.lab.oauth.TokenProvider;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${rabbitmq.host}")
    private String hostname;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.vhost}")
    private String vhost;

    @Autowired
    private TokenProvider tokenProvider;


    /**
     * RabbitMQ connection & channels configuration using OAuth2
     * @return Customized Oauth2 connectionFactory
     */
    @Bean
    public CachingConnectionFactory  cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(hostname);
        cachingConnectionFactory.setPort(port);
        cachingConnectionFactory.setVirtualHost(vhost);
        cachingConnectionFactory.setUsername("jwt");
        cachingConnectionFactory.setPassword(tokenProvider.getJwtToken());

        // Connexion listener to manage specific behavior on connection lost
        cachingConnectionFactory.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                log.info("RabbitMQ connection established !");
            }

            @Override
            public void onClose(Connection connection) {
                log.warn("RabbitMQ connection closed !");
                // Refresh the JWT token for next connection
                cachingConnectionFactory.setPassword(tokenProvider.getJwtToken());
            }

            @Override
            public void onShutDown(ShutdownSignalException signal){
                log.warn("RabbitMQ connection shutdown :  {} !", signal.getMessage());
            }

            @Override
            public void onFailed(Exception exception) {
                log.error("RabbitMQ connection failed :  {} !", exception.getMessage());
            }
        });
        return cachingConnectionFactory;
    }

    /**
     * Specific listenerContainerFactory that implement a Spring retry mechanism
     * @param cachingConnectionFactory Custom cachingConnectionFactory
     * @return ListenerContainerFactory with Spring retry mechanism
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryWithRetryManagedBySpring(CachingConnectionFactory cachingConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(2);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(5000, 2.0, 10000) // initialInterval, multiplier, maxInterval
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }

    /**
     * Standard listenerContainerFactory using the custom cachingConnectionFactory
     * @param cachingConnectionFactory Custom cachingConnectionFactory
     * @return Standard ListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory  cachingConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(2);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplateJson(CachingConnectionFactory cachingConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}