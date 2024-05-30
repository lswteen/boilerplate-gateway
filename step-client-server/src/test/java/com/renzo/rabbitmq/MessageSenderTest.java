package com.renzo.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.core.AmqpTemplate;

import static org.mockito.Mockito.verify;

@SpringBootTest
class MessageSenderTest {

    @Autowired
    private MessageSender sender;

    @MockBean
    private AmqpTemplate rabbitTemplate;

    //@Test
    public void testSendLogMessage() throws Exception {
        sender.sendLogMessage();
        verify(rabbitTemplate).convertAndSend("", "PageLog_TEST", "{\"svc_nation_cd\":\"SG\",\"app_no\":1617,\"logtype\":3,\"contexturl\":\"http://in-psg.qoo10.com/test/mqTest\",\"useragent\":\"Mozilla/5.0\",\"postion\":0,\"location\":\"PsgGiosisRmpTest.giosisRmq_connection_test\",\"targetserver\":\"psg-local-server\",\"message\":\"rqm test message4\",\"detailmessage\":\"rqm test detail message4\",\"page_no\":0,\"device_info\":\"psg-api\",\"log_dt\":\"2024-05-07 20:02:00\",\"client_ip\":\"10.103.128.76\"}");
    }

}