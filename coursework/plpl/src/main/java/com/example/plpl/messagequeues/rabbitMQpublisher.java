package com.example.plpl.messagequeues;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import com.example.plpl.externalrestservices.randomid;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class rabbitMQpublisher {



    // Notice how the exchange types have been defined as enums and how they get converted to strings on line 45
    private enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS}


    // Set this for topic or direct exchanges. Leave empty for fanout.
    private final static String TOPIC_KEY_NAME = ""; // For topic the format is keyword1.keyword2.keyword3. and so on.

    //@GetMapping("api/v1/submitproposal")
    public static void publish(String message, String EXCHANGE_NAME) throws Exception, JSONException {

        // Connect to the RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); // Add here the IP provided by your tutor
        factory.setUsername("student"); // Add here the username provided by your tutor
        factory.setPassword("COMP30231"); // Add here the password provided by your tutor


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //channel.exchangeDelete(EXCHANGE_NAME); // sometimes you must delete an existing exchange
            // Declare the exchange you want to connect your queue to
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase()); // 2nd parameter: fanout, direct, topic, headers
            //String message = "{\"data\": {\"fields\": [{\"name\":\"john\",\"age\":22}]}}";
            // Publish a message to the exchange
            // This message will remain there until a client consumes it ...
            // Notice any difference in behavior as opposed to our previous socket client-server app?
            channel.basicPublish(EXCHANGE_NAME,
                    TOPIC_KEY_NAME, // This parameter is used for the routing key, which is usually used for direct or topic queues.
                    new AMQP.BasicProperties.Builder()
                            .contentType("text/plain")
                            .deliveryMode(2)
                            .priority(1)
                            .userId("student")
                            //.expiration("60000")
                            .build(),
                    message.getBytes(StandardCharsets.UTF_8));


            System.out.println(" [x] Sent '" + TOPIC_KEY_NAME + ":" + message + "'");
        }
    }
}

