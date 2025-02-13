package com.khelnor.lab.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.khelnor.lab.event.model.Movie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("producer")
public class MessageController {

    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @GetMapping("/send-text")
    public String sendText(@RequestParam String message) {
        messageProducer.sendMessage(message);
        return "Message sent: " + message;
    }

    @PostMapping("/send-object")
    public String sendObject(@RequestBody Movie movie) {
        messageProducer.sendObject(movie);
        return "Object sent: " + movie;
    }

    @PostMapping("/send-json")
    public String sendJson(@RequestBody Movie movie) {
        messageProducer.sendJson(movie);
        return "JSON sent: " + movie;
    }

    @PostMapping("/send-xml")
    public String sendXml(@RequestBody Movie movie) throws JsonProcessingException {
        String xmlMessage = messageProducer.sendXml(movie);
        return "XML sent: " + xmlMessage;
    }
}
