package com.example.plpl.messagequeues;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONException;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

@RestController
public class rabbitMQsubscriber {


    private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};

    // Notice how the queue and exchange names are defined as constants for easy reuse

    private final static String QUEUE_NAME = "hamzahsqueue";

    // Set this for topic or direct exchanges. Leave empty for fanout.
    private final static String TOPIC_KEY_NAME = ""; // For direct use full name. For topic use * to match one word or # to match multiple: *.blue, red.#, etc.

    public static ArrayList<String> themessage;

    public static void subscribe(String EXCHANGE_NAME) throws Exception, JSONException {

        // Connect to the RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("152.71.155.95");
        factory.setUsername("student");
        factory.setPassword("COMP30231");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the exchange you want to connect your queue to
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase()); // 2nd parameter: fanout, direct, topic, headers
        // Get an existing server-declared queue to connect to the exchange
        // IMPORTANT: This only works if the queue is already defined on the RabbitMQ server (through the web-UI for instance)
        // Try uncommenting line 44 and commenting out line 46 and see what happens (replace on line 46 constant QUEUE_NAME with queueName to fix the compilation error
        //String queueName = channel.queueDeclare().getQueue();
        // Declare a subscriber-defined queue
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // Link the queue to the exchange
        /*
            Special characters can be used in routing keys:
            - using "#" means the queue will receive all messages (like  fanout).
            - using * can replace one word in the routing key / topic.
        */
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, TOPIC_KEY_NAME); // The last parameter is the routing key usually used for direct or topic queues

        System.out.println(" [*] Waiting for " + TOPIC_KEY_NAME +  " messages. To exit press CTRL+C");
        File damnmessages = new File("themessage.txt");
        FileWriter myWriter = new FileWriter("themessage.txt");

        File intentmessages = new File("intentmessage.txt");
        FileWriter myOtherWriter = new FileWriter("intentmessage.txt");

        if(EXCHANGE_NAME == "TRAVEL_OFFERS"){
            // This code block indicates a callback which is like an event triggered ONLY when a message is received
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");


                myWriter.write(message + "\n");
                myWriter.flush();

                System.out.println(" Received proposal : '" + message + "'");

            };

            //String themessage = messages.get();
            // Consume messages from the queue by using the callback
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        }

        else if (EXCHANGE_NAME == "TRAVEL_INTENT") {

            // This code block indicates a callback which is like an event triggered ONLY when a message is received
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                myOtherWriter.write(message + "\n");
                myOtherWriter.flush();

                System.out.println(" Received Intent : '" + message + "'");

            };

            //String themessage = messages.get();
            // Consume messages from the queue by using the callback
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }

    }
}



