package com.khelnor.lab.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.khelnor.lab.model.Movie;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("producer")
@AllArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping("/send-error")
    public String sendError(@RequestBody Movie movie) {
        messageProducer.sendError(movie);
        return "JSON sent: " + movie;
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