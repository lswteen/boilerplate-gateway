package com.renzo.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendLogMessage() throws JsonProcessingException {
        Map<String, Object> log = new HashMap<>();
        log.put("svc_nation_cd", "SG");
        log.put("app_no", 1617);
        log.put("logtype", 3);
        log.put("contexturl", "http://in-psg.qwe.com/test/mqTest");
        log.put("useragent", "Mozilla/5.0");
        log.put("postion", 0);
        log.put("location", "PsgewrRmpTest.giosisRmq_connection_test");
        log.put("targetserver", "psg-local-server");
        log.put("message", "rqm test message4");
        log.put("detailmessage", "rqm test detail message4");
        log.put("page_no", 0);
        log.put("device_info", "psg-api");
        log.put("log_dt", "2024-05-07 20:02:00");
        log.put("client_ip", "414.103.128.76");

        ObjectMapper mapper = new ObjectMapper();
        String sendMessage = mapper.writeValueAsString(log);

        this.rabbitTemplate.convertAndSend("", "PageLog_TEST", sendMessage);
    }
}
