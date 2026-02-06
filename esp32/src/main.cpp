#include <Arduino.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include "secrets.h"

// Configuración de pines y sensor
const int ONE_WIRE_BUS = 4;
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);


WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi() {
    delay(10);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println("\nWiFi Conectado");
    Serial.println(ssid);
}

void callback(char* topic, byte* payload, unsigned int length) {
    String message = "";
    for (int i = 0; i < length; i++) { message += (char)payload[i]; }

    if (String(topic) == "casa/bodega/chimenea/restart" && message == "1") {
        Serial.println("¡Orden de reinicio recibida!");
        delay(500);
        ESP.restart(); // Reinicia el hardware
    }
}

void reconnect() {
    while (!client.connected()) {
        if (client.connect("ESP32_Bodega", mqtt_user, mqtt_password, "casa/bodega/chimenea/status", 1, true, "offline")) {
            client.publish("casa/bodega/chimenea/status", "online", true);
            client.subscribe("casa/bodega/chimenea/restart");
            Serial.println("Conectado a MQTT");
        } else {
            delay(5000);
        }
    }
}


void setup() {
    Serial.begin(115200);
    setup_wifi();
    client.setServer(mqtt_server, 1883);
    
    // Iniciar el sensor de temperatura
    sensors.begin();

    client.setServer(mqtt_server, 1883);
    client.setCallback(callback);
}



void loop() {
    if (!client.connected()) {
        reconnect();
    }
    client.loop();

    
    static unsigned long lastMsg = 0;
    if (millis() - lastMsg > 10000) {
        lastMsg = millis();

        
        sensors.requestTemperatures(); 
        float tempC = sensors.getTempCByIndex(0);

        if (tempC != DEVICE_DISCONNECTED_C) {
            
            JsonDocument doc;
            doc["temperatura"] = tempC;
            doc["bomba"] = (tempC > 60.0) ? 1 : 0; 

            char buffer[128];
            serializeJson(doc, buffer);

            
            client.publish("casa/bodega/chimenea/data", buffer);
            Serial.print("Dato enviado: ");
            Serial.println(buffer);
        } else {
            Serial.println("Error: No se pudo leer el sensor DS18B20");
        }
    }
}