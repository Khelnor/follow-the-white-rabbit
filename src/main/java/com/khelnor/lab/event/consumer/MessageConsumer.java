package com.khelnor.lab.event.consumer;

import com.khelnor.lab.event.RabbitMQConfig;
import com.khelnor.lab.model.Movie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MessageConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TEST)
    public void listen(Movie movie, @Headers Map<String, Object> headers) {
        log.info("Message received !");
        log.info("Message ID: {}", headers.get(AmqpHeaders.MESSAGE_ID));
        log.info("Application ID: {}", headers.get(AmqpHeaders.APP_ID));
        log.info("Timestamp: {}", headers.get(AmqpHeaders.TIMESTAMP));
        log.info("Payload: {}", movie);

        if ("ERROR".equalsIgnoreCase((String) headers.get(AmqpHeaders.APP_ID))) {
            log.error("Error during consuming the message with id {}", headers.get(AmqpHeaders.MESSAGE_ID));
            throw new RuntimeException("Fake error");
        } else {
            log.info("Message with id {} consumed successfully !", headers.get(AmqpHeaders.MESSAGE_ID));
        }
    }
}