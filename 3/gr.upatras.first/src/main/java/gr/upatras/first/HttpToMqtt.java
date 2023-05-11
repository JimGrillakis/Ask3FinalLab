package gr.upatras.first;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import spark.*;

public class HttpToMqtt {

    public static void main(String[] args) {

        // Set up MQTT client
        String brokerUrl = "tcp://mqtt.eclipse.org:1883";
        String topicName = "my/topic";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId(), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + brokerUrl);
            client.connect(connOpts);
            System.out.println("Connected");

            // Set up HTTP server to listen for POST requests
            Spark.post("/publish", (req, res) -> {
                String payload = req.body();
                System.out.println("Received payload: " + payload);
                MqttMessage message = new MqttMessage(payload.getBytes());
                client.publish(topicName, message);
                System.out.println("Published message to topic: " + topicName);
                return "OK";
            });

        } catch (MqttException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
