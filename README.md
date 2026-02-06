# 🧯 Monitorización de Caldera IoT

Este proyecto es un ecosistema IoT completo para la monitorización de una chimenea calefactora en una casa mediante comunicación bidireccional entre hardware embebido y una aplicación móvil, utilizando el protocolo **MQTT** para garantizar una comunicación ligera, rápida y eficiente. El flujo de información se divide en tres capas principales:

1.  **Hardware (Arduino/ESP32):** Captura datos de sensores y los publica en la red.
2.  **Mensajería (Mosquitto):** El broker MQTT que orquestra la comunicación entre los nodos.
3.  **Mobile (Android App):** Interfaz de usuario para monitoreo y control remoto.

---

## 🔄 Flujo de Datos (Arquitectura)
El sistema sigue el siguiente camino de información:

`[Sensor DS18B20] --(OneWire)--> [ESP32] --(WiFi/JSON)--> [Mosquitto Broker (Docker)] <--> [Android App]`

---

## 📂 Estructura del Repositorio

* `/android-app`: Proyecto de Android Studio (Java/Kotlin).
* `/esp32`: Sketches de Arduino (.ino) y definición de pines.
* `/broker-mqtt`: Archivos de configuración `.conf` y scripts de despliegue.
* `/docs`: Diagramas de conexión y documentación técnica.

---

## 🚀 Componentes del Sistema

### 1. ESP32 (Hardware & Firmware)
El controlador principal se encarga de leer la temperatura y enviar los datos a la red.
* **Sensor:** DS18B20 (Pin GPIO4).
* **Comunicación:** MQTT (PubSubClient) con carga útil en formato JSON.
* **Funciones:** Envío de temperatura cada 10s y reinicio remoto mediante suscripción a tópicos de control.
* **Seguridad:** Las credenciales de WiFi y MQTT se gestionan en un archivo `secrets.h` (ignorado por Git).

### 2. Broker MQTT (Docker)
El corazón de las comunicaciones funciona sobre **Mosquitto** dentro de un contenedor Docker para facilitar su despliegue en cualquier servidor o Raspberry Pi.
* **Configuración:** Incluye persistencia de datos y autenticación por usuario/contraseña.
* **Despliegue:**
```
cd broker-mqtt
docker-compose up -d
```

### 3. App Android
Interfaz de usuario para visualizar en tiempo real el estado de la bodega.
* **Tecnología:** Nativo (Android Studio).
* **Visualización:** Temperatura actual y estado de la bomba (activación automática > 60°C).

---

## 🛠️ Instalación y Configuración

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/pabloglezs/iot-fireplace-temp-mqtt.git
    ```

2.  **Configurar el ESP32:**
    * Navega a `esp32/include/`.
    * Crea un archivo `secrets.h` basado en el siguiente esquema:
        ```cpp
        const char* ssid = "TU_WIFI";
        const char* password = "TU_PASSWORD";
        const char* mqtt_server = "TU_IP_O_DOMINIO";
        const char* mqtt_user = "usuario";
        const char* mqtt_password = "password";
        ```
    * Carga el código usando PlatformIO.
    * Nota: El archivo secrets.h está pre-configurado en el .gitignore para evitar fugas de credenciales.

3.  **Levantar el Servidor:**
    * Asegúrate de tener Docker instalado.
    * Ejecuta `docker-compose up -d` en la carpeta `broker-mqtt`.

---

## 📡 Tópicos MQTT Principales

* `casa/bodega/chimenea/data`: Publicación de temperatura y estado de bomba (JSON).
* `casa/bodega/chimenea/status`: Estado de conexión (LWT - online/offline).
* `casa/bodega/chimenea/restart`: Suscripción para reinicio remoto (mensaje "1").
