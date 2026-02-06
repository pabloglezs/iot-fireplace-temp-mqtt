package com.example.mqttsoto28;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MiMqttClient {

    private static Mqtt5BlockingClient instance;
    private static final String BROKER_URL = "92.222.10.128";
    private static final String USERNAME = "pablo";
    private static final String PASSWORD = "Colegiado97!";


    public static Mqtt5BlockingClient getInstance() {
        if (instance == null) {
            instance = Mqtt5Client.builder()
                    .identifier("android-device-" + UUID.randomUUID())
                    .serverHost(BROKER_URL)
                    .serverPort(1883)
                    // Añadimos usuario y contraseña aquí
                    .simpleAuth()
                    .username(USERNAME)
                    .password(PASSWORD.getBytes(StandardCharsets.UTF_8))
                    .applySimpleAuth()
                    .buildBlocking();
        }
        return instance;
    }
}
