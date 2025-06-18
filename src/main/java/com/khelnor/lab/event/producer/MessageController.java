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

    @PostMapping("/send-error/{queue}")
    public String sendError(@RequestBody Movie movie, @PathVariable String queue) {
        messageProducer.sendError(movie, queue);
        return "JSON sent: " + movie;
    }

    @PostMapping("/send-json/{queue}")
    public String sendJson(@RequestBody Movie movie, @PathVariable String queue) {
        messageProducer.sendJson(movie, queue);
        return "JSON sent: " + movie;
    }

    @PostMapping("/send-xml/{queue}")
    public String sendXml(@RequestBody Movie movie, @PathVariable String queue) throws JsonProcessingException {
        String xmlMessage = messageProducer.sendXml(movie, queue);
        return "XML sent: " + xmlMessage;
    }
}