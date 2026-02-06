# 📡 Mosquitto MQTT Broker

Configuración del servidor de mensajería para el sistema de control de temperatura de la bodega. Este contenedor gestiona la comunicación entre el **ESP32** y la **App Android**.

---

## 📂 Estructura del Módulo

* **docker-compose.yml**: Configuración del servicio Docker.
* **config/mosquitto.conf**: Configuración técnica del Broker (puertos, persistencia).
* **config/password.txt**: Archivo de credenciales (No incluido en el repositorio por seguridad).
* **data/**: Base de datos de persistencia (Mensajes retenidos).
* **log/**: Historial de eventos del servidor.

---

## 🚀 Despliegue Rápido

Para levantar el broker en el VPS, navega hasta esta carpeta y ejecuta:

`sudo docker-compose up -d`

### Comandos Útiles
* Ver estado: `sudo docker ps`
* Ver logs en tiempo real: `sudo docker logs -f mosquitto`
* Reiniciar servicio: `sudo docker-compose restart`

---

## 🔒 Seguridad y Configuración

El broker está configurado bajo las siguientes reglas:
1. **Puerto 1883**: Acceso estándar para dispositivos.
2. **Acceso Protegido**: `allow_anonymous false`. Se requiere usuario y contraseña.
3. **Persistencia**: Los datos se guardan en `./data/mosquitto.db` cada 30 minutos.
4. **Mapeo de Volúmenes**: Los archivos de configuración en el VPS están sincronizados con el contenedor.

---

## 🛠️ Requisitos de Instalación

1. Docker y Docker Compose instalados en el sistema.
2. Crear manualmente el archivo `config/password.txt` antes del primer inicio:

`touch config/password.txt`

Para añadir un usuario ejecute:
`sudo docker exec -it mosquitto mosquitto_passwd -b /mosquitto/config/password.txt usuario contraseña`

---
*Nota: Este repositorio utiliza un .gitignore estricto para evitar la subida de datos sensibles y archivos de sistema del VPS.*
