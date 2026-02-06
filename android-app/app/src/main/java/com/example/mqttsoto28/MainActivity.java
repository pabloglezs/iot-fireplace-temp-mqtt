package com.example.mqttsoto28;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView txtTemp, txtEstado, txtConexion;
    private View indicatorStatus;
    private ImageView imgLlama;
    private ObjectAnimator pulseAnimatorStatus, pulseAnimatorLlama;

    // Añadimos una bandera para evitar intentos simultáneos
    private boolean isConnecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 2. Forzar que el contenido se dibuje detrás de las barras (arriba y abajo)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 3. Hacer las barras totalmente transparentes y QUITAR el contraste forzado
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setStatusBarContrastEnforced(false);
        }

        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // 4. Configurar iconos claros/oscuros según tu fondo
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // false para iconos blancos (si tu app es oscura), true para iconos negros (si es clara)
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        //inicializarToolbar();
        inicializarFloatingButton();
        inicializarReferenciasUi();

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        pulseAnimatorLlama = startStopPulse(pulseAnimatorLlama, imgLlama, false);
        pulseAnimatorStatus = startStopPulse(pulseAnimatorStatus, indicatorStatus, false);

        // Iniciamos la configuración
        new Thread(this::configurarMqtt).start();
    }

    private void inicializarReferenciasUi() {
        txtTemp = findViewById(R.id.txtTemp);
        txtEstado = findViewById(R.id.txtEstado);
        txtConexion = findViewById(R.id.txtConexion);
        imgLlama = findViewById(R.id.imgLlama);
        indicatorStatus = findViewById(R.id.indicatorStatus);
    }

    private ObjectAnimator startStopPulse(ObjectAnimator animator, View view, boolean start) {
        // Si ya existe un animador, lo cancelamos siempre
        if (animator != null) {
            animator.cancel();
            if (view != null) view.setAlpha(1f);
        }

        if (start && view != null) {
            // Creamos el nuevo animador y lo iniciamos
            ObjectAnimator newAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f);
            newAnimator.setDuration(1000);
            newAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            newAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            newAnimator.start();
            return newAnimator; // Devolvemos el objeto creado para guardarlo fuera
        }

        return null; // Si paramos la animación, devolvemos null
    }


    private void inicializarToolbar() {
        /*MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Manejar clics en el menú
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                // Abrir una actividad de ajustes o mostrar un mensaje
                Toast.makeText(this, "Ajustes del Broker", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });*/
    }

    private void inicializarFloatingButton(){
        FloatingActionButton btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(v -> {
            Mqtt5BlockingClient client = MiMqttClient.getInstance();
            // Verificamos conexión antes de publicar
            if (client != null && client.getState().isConnected()) {
                client.toAsync().publishWith()
                        .topic("casa/bodega/chimenea/restart")
                        .payload("1".getBytes(StandardCharsets.UTF_8))
                        .send();
                Toast.makeText(this, "Enviando orden de reinicio...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay conexión con el ESP32", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarMqtt() {
        // Evitar múltiples hilos intentando conectar a la vez
        if (isConnecting) return;

        Mqtt5BlockingClient client = MiMqttClient.getInstance();

        // PASO CRUCIAL: Verificar si ya está conectado
        if (client.getState().isConnected()) {
            Log.d("MQTT", "Ya estaba conectado. Saltando conexión y re-suscribiendo.");
            actualizarUiConectado();
            suscribirTopicos(client);
            return;
        }

        try {
            isConnecting = true;
            runOnUiThread(() -> txtConexion.setText("Conectando..."));

            client.connect();

            isConnecting = false;
            actualizarUiConectado();
            suscribirTopicos(client);

        } catch (Exception e) {
            isConnecting = false;
            Log.e("MQTT", "Error al conectar: " + e.getMessage());
            runOnUiThread(() -> {
                txtConexion.setText("Error de conexión");
                indicatorStatus.setBackgroundResource(R.drawable.bg_status_dot_red);
            });
        }
    }

    private void actualizarUiConectado() {
        runOnUiThread(() -> {
            txtConexion.setText("Conectado al Servidor Central");
            txtConexion.setTextColor(Color.parseColor("#636E72"));
            indicatorStatus.setBackgroundResource(R.drawable.bg_status_dot); // Verde
        });
    }

    private void suscribirTopicos(Mqtt5BlockingClient client) {
        client.toAsync().subscribeWith()
                .topicFilter("casa/bodega/chimenea/#")
                .callback(publish -> {
                    String topic = publish.getTopic().toString();
                    String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

                    runOnUiThread(() -> procesarMensaje(topic, payload));
                })
                .send();
    }

    private void procesarMensaje(String topic, String payload) {
        // --- CASO 1: STATUS DEL ESP32 ---
        if (topic.equals("casa/bodega/chimenea/status")) {
            if (payload.equalsIgnoreCase("offline")) {
                txtConexion.setText("Sensor desconectado (Offline)");
                txtConexion.setTextColor(Color.RED);
                indicatorStatus.setBackgroundResource(R.drawable.bg_status_dot_red);
                txtEstado.setText("Sin señal del sensor");
                pulseAnimatorStatus = startStopPulse(pulseAnimatorStatus, indicatorStatus, true);
                pulseAnimatorLlama = startStopPulse(pulseAnimatorLlama, imgLlama, false);
            } else {
                actualizarUiConectado();
                pulseAnimatorStatus = startStopPulse(pulseAnimatorStatus, indicatorStatus, false);
            }
        }

        // --- CASO 2: DATOS JSON ---
        if (topic.equals("casa/bodega/chimenea/data")) {
            try {
                JSONObject json = new JSONObject(payload);
                double temp = json.getDouble("temperatura");
                // int bomba = json.getInt("bomba"); // Por si lo usas después

                txtTemp.setText(String.format("%.1f °C", temp));
                txtEstado.setText(temp >= 20 ? "Chimenea encendida" : "Chimenea apagada");
                imgLlama.clearColorFilter();

                if (temp < 20) {
                    txtTemp.setTextColor(Color.GRAY);
                    imgLlama.setImageResource(R.drawable.ic_fire_off);
                    imgLlama.setColorFilter(Color.parseColor("#B2BEC3"));
                    pulseAnimatorLlama = startStopPulse(pulseAnimatorLlama, imgLlama, false);
                } else if (temp >= 20 && temp <= 75) {
                    txtTemp.setTextColor(Color.WHITE);
                    imgLlama.setImageResource(R.drawable.ic_fire_on);
                    pulseAnimatorLlama = startStopPulse(pulseAnimatorLlama, imgLlama, true);
                } else {
                    imgLlama.setImageResource(R.drawable.ic_fire_alert);
                    txtTemp.setTextColor(Color.RED);
                    lanzarNotificacionPeligro();
                }
            } catch (JSONException e) {
                Log.e("MQTT_JSON", "Error JSON: " + e.getMessage());
            }
        }
    }

    private void lanzarNotificacionPeligro() {
        // Tu lógica de notificación aquí
    }
}