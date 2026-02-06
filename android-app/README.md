# 🔥 Chimenea Control - Android App

Aplicación nativa para la monitorización en tiempo real de la temperatura de la bodega y control remoto del sistema.

---

## 🛠️ Stack Tecnológico
* **Lenguaje**: Java / Android SDK.
* **Comunicación**: MQTT (HiveMQ Client).
* **Interfaz**: Material Design 3 (UI inmersiva Edge-to-Edge).
* **Formato de datos**: JSON.

---

## 📱 Funcionalidades
* **Monitorización**: Visualización de temperatura en tiempo real.
* **Alertas Visuales**: 
  - 🧊 **Gris**: Apagada (< 20°C).
  - 🔥 **Naranja**: Encendida (Pulso de animación).
  - 🚨 **Rojo**: Alerta de alta temperatura (> 75°C).
* **Control**: Botón de reinicio remoto del ESP32 vía MQTT.
* **Estado de Conexión**: Indicador dinámico de estado del Broker y del sensor (LWT).

---

## ⚙️ Configuración del Broker
Para conectar la app a tu servidor, asegúrate de configurar en la clase `MiMqttClient`:
```java
public class MiMqttClient {

    private static Mqtt5BlockingClient instance;
    private static final String BROKER_URL = "ip_de_tu_host";
    private static final String USERNAME = "usuario_mqtt";
    private static final String PASSWORD = "password_mqtt";


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
```

---

## 🚀 Instalación
1. Clonar el repositorio.
2. Abrir con **Android Studio**.
3. Sincronizar Gradle.
4. Habilitar "Instalación vía USB" en tu dispositivo y ejecutar.

---
*Nota: La aplicación utiliza insets de sistema personalizados para una experiencia de pantalla completa total.*
