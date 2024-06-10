package com.renzo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renzo.rabbitmq.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    private final MessageSender messageSender;

    @Autowired
    public MessageController(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @GetMapping("/send-log")
    public ResponseEntity<String> sendLogMessage() {
        try {
            messageSender.sendLogMessage();
            return ResponseEntity.ok("Log message sent successfully");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Failed to send log message: " + e.getMessage());
        }
    }
}
