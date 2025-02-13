package com.khelnor.lab.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.khelnor.lab.event.RabbitMQConfig;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitTemplate rabbitTemplateJson;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);

    }

    public void sendObject(Object object) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, object);
    }

    public void sendJson(Object object) {
        rabbitTemplateJson.convertAndSend(RabbitMQConfig.QUEUE_NAME, object);
    }

    public String sendXml(Object object) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setAppId("rabbit-demo"); // Setting application-id
        messageProperties.setTimestamp(new Date()); // Setting timestamp
        XmlMapper xmlMapper = new XmlMapper();
        String xmlMessage= xmlMapper.writeValueAsString(object);
        Message message = new Message(xmlMessage.getBytes(StandardCharsets.UTF_8), messageProperties);
        rabbitTemplate.send(RabbitMQConfig.QUEUE_NAME, message);
        return xmlMessage;
    }

}