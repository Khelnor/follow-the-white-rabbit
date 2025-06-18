package com.khelnor.lab.event.consumer;

import com.khelnor.lab.event.configuration.RabbitDemoQueueConfig;
import com.khelnor.lab.model.Movie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MessageConsumer {

    private final RabbitTemplate rabbitTemplateJson;

    @RabbitListener(queues = RabbitDemoQueueConfig.QUEUE_TEST, containerFactory = "rabbitListenerContainerFactoryWithRetryManagedBySpring")
    private void listen1(Movie movie, @Headers Map<String, Object> headers) {
        processMessage(movie, headers);
    }

    @RabbitListener(queues = RabbitDemoQueueConfig.QUEUE_TEST2, containerFactory = "rabbitListenerContainerFactory")
    private void listen2(Movie movie, @Headers Map<String, Object> headers, MessageProperties properties) {
        if ((Long)headers.get(AmqpHeaders.RETRY_COUNT) > 3) {
            log.error("Send message {} to DLQ !", headers.get(AmqpHeaders.MESSAGE_ID));
            sendToDLQ(movie, properties);
        }
        else {
            processMessage(movie, headers);
        }
    }

    private void processMessage(Movie movie, Map<String, Object> headers){
        log.info("Message received !");
        log.info("Message ID: {}", headers.get(AmqpHeaders.MESSAGE_ID));
        log.info("Application ID: {}", headers.get(AmqpHeaders.APP_ID));
        log.info("Timestamp: {}", headers.get(AmqpHeaders.TIMESTAMP));
        log.info("Retry count: {}", headers.get(AmqpHeaders.RETRY_COUNT));
        log.info("Payload: {}", movie);

        if ("ERROR".equalsIgnoreCase((String) headers.get(AmqpHeaders.APP_ID))) {
            log.error("Error during consuming the message with id {}", headers.get(AmqpHeaders.MESSAGE_ID));
            throw new RuntimeException("Fake error");
        } else {
            log.info("Message with id {} consumed successfully !", headers.get(AmqpHeaders.MESSAGE_ID));
        }
    }

    private void sendToDLQ(Movie movie, MessageProperties properties) {
        Message newMessage = MessageBuilder.withBody(rabbitTemplateJson.getMessageConverter().toMessage(movie, properties).getBody())
                .copyProperties(properties)
                .build();

        rabbitTemplateJson.send(RabbitDemoQueueConfig.DLQ_EXCHANGE, RabbitDemoQueueConfig.DLQ_ROUTING_KEY, newMessage);
    }
}