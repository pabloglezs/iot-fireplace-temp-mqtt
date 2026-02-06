# 🌡️ ESP32 Fireplace Sensor

Firmware para el nodo sensor de temperatura de la bodega. Utiliza un **ESP32** para capturar datos y enviarlos mediante el protocolo **MQTT**.

---

## 🛠️ Tecnologías y Librerías

* **Framework**: PlatformIO.
* **MQTT**: PubSubClient.
* **Sensor**: DS18B20
* **Comunicación**: Wi-Fi (2.4GHz).

---

## 📂 Estructura del Proyecto

* **src/**: Contiene el código fuente principal (`main.cpp`).
* **lib/**: Librerías específicas del proyecto.
* **platformio.ini**: Configuración del entorno, placa y dependencias.

---

## 🔐 CONFIGURACIÓN OBLIGATORIA (secrets.h)

Por razones de seguridad, las credenciales no están incluidas en el repositorio. **Es necesario crear un archivo llamado secrets.h** en la carpeta **include/** con el siguiente formato:
```
#define WIFI_SSID "TU_WIFI_NOMBRE"
#define WIFI_PASS "TU_WIFI_PASSWORD"
#define MQTT_USER "TU_USUARIO_MOSQUITTO"
#define MQTT_PASS "TU_PASSWORD_MOSQUITTO"
```
---

## 🚀 Compilación y Carga

Si utilizas **PlatformIO CLI**, puedes usar los siguientes comandos:

* **Compilar:** `pio run`
* **Subir código:** `pio run --target upload`
* **Monitor Serie:** `pio device monitor`
