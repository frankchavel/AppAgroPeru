package unc.edu.pe.agroper;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import Model.Cultivo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Notificaciones.NotificacionCosechaWorker;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class RegistroCultivoActivity extends AppCompatActivity {

    private static final int REQ_ZONA = 1001;

    // Views
    private TextView tvZonaNombre, tvZonaCoords;
    private TextView tvFechaSiembra, tvFechaCosecha;

    private EditText etNombre, etDescripcion, etArea, etRiego;
    private AutoCompleteTextView actvEstado;
    private MaterialButton btnGuardar, btnSeleccionarZona;


    // Data
    private int zonaID = 0;

    private int zonaSeleccionadaId = 0;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cultivo);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        findViewById(R.id.btn_lista).setOnClickListener(v -> {
            Intent intent = new Intent(this, Lista_Cultivos_Activity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_seleccionar_insumo).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarRecursoActivity.class);
            startActivity(intent);
        });
        initViews();
        configurarDropdownEstado();
        configurarEventos();
    }

    private void initViews() {

        etNombre = findViewById(R.id.et_nombre_cultivo);
        etDescripcion = findViewById(R.id.et_descripcion);
        etArea = findViewById(R.id.et_area);
        etRiego = findViewById(R.id.et_riego);

        tvFechaSiembra = findViewById(R.id.tv_fecha_siembra);
        tvFechaCosecha = findViewById(R.id.tv_fecha_cosecha);

        tvZonaNombre = findViewById(R.id.tv_zona_nombre);
        tvZonaCoords = findViewById(R.id.tv_zona_coords);


        actvEstado = findViewById(R.id.actv_estado);

        btnGuardar = findViewById(R.id.btn_guardar);
        btnSeleccionarZona = findViewById(R.id.btn_seleccionar_zona);
    }

    private void configurarEventos() {

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        btnSeleccionarZona.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocalizacionCultivoActivity.class);
            startActivityForResult(intent, REQ_ZONA);
        });


        tvFechaSiembra.setOnClickListener(v -> mostrarDatePicker(tvFechaSiembra));
        tvFechaCosecha.setOnClickListener(v -> mostrarDatePicker(tvFechaCosecha));

        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void mostrarDatePicker(TextView campo) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    campo.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQ_ZONA) {
            zonaID = data.getIntExtra("zona_id", 0);
            String nombre = data.getStringExtra("zona_nombre");
            double lat = data.getDoubleExtra("zona_lat", 0);
            double lng = data.getDoubleExtra("zona_lng", 0);

            tvZonaNombre.setText(nombre != null ? nombre : "Zona #" + zonaID);
            tvZonaCoords.setText(String.format(Locale.US, "%.5f, %.5f", lat, lng));
        }
    }

    private void validarYGuardar() {

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String riegoStr = etRiego.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa el nombre");
            return;
        }

        if (zonaID == 0) {
            snack("Selecciona una zona");
            return;
        }

        Cultivo cultivo = new Cultivo();

        cultivo.setZonaID(zonaID);
        cultivo.setUsuarioID(0);
        cultivo.setProductoAgricolaID(1); // IMPORTANTE
        cultivo.setNombreCultivo(nombre);
        cultivo.setDescripcion(descripcion);
        cultivo.setFechaCosechaReal(null);
        cultivo.setProductoAgricolaID(1);

        cultivo.setAreaCultivo(
                areaStr.isEmpty() ? 0 : Double.parseDouble(areaStr)
        );

        cultivo.setTiempoRiegoPromedio(
                riegoStr.isEmpty() ? 0 : Integer.parseInt(riegoStr)
        );

        cultivo.setFechaRegistro(OffsetDateTime.now().toString());

        cultivo.setEstado("Activo"); // CORREGIDO

        String fechaSiembraUI = tvFechaSiembra.getText().toString().trim();
        String fechaCosechaUI = tvFechaCosecha.getText().toString().trim();

        if (fechaSiembraUI.isEmpty() || fechaCosechaUI.isEmpty()) {
            snack("Selecciona fecha de siembra y cosecha");
            return;
        }

        String fechaSiembraAPI = convertirFechaParaAPI(fechaSiembraUI);
        String fechaCosechaAPI = convertirFechaParaAPI(fechaCosechaUI);

        if (fechaSiembraAPI == null || fechaCosechaAPI == null) {
            snack("Formato de fecha inválido");
            return;
        }

        cultivo.setFechaSiembra(fechaSiembraAPI);
        cultivo.setFechaCosechaEstimada(fechaCosechaAPI);

        enviarCultivo(cultivo);
    }

    private void enviarCultivo(Cultivo cultivo) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            snack("Usuario no autenticado");
            return;
        }

        cultivo.setCorreoUsuario(user.getEmail()); // 🔥 ESTA ES LA SOLUCIÓN REAL

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        apiService.crearCultivo(cultivo).enqueue(new Callback<Cultivo>() {

            @Override
            public void onResponse(Call<Cultivo> call, Response<Cultivo> response) {

                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Cultivo");

                if (response.isSuccessful() && response.body() != null) {

                    Cultivo cultivoGuardado = response.body();

                    // 🔔 Programar notificación 1 día antes
                    programarNotificacion(
                            cultivoGuardado.getNombreCultivo(),
                            cultivoGuardado.getFechaCosechaEstimada()
                    );

                    Toast.makeText(RegistroCultivoActivity.this,
                            "Cultivo guardado correctamente",
                            Toast.LENGTH_LONG).show();

                    finish();

                } else {

                    try {
                        String errorBody = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Error desconocido";

                        Log.e("ERROR_SERVIDOR", errorBody);
                        snack("Error: " + errorBody);

                    } catch (Exception e) {
                        snack("Error servidor: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Cultivo> call, Throwable t) {

                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Cultivo");

                snack("Error conexión: " + t.getMessage());
            }
        });
    }


    private void configurarDropdownEstado() {
        String[] estados = {"Planificado", "En curso", "Cosechado", "Perdido"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, estados);
        actvEstado.setAdapter(adapter);
        actvEstado.setText(estados[1], false);
    }
    private String convertirFechaParaAPI(String fechaUI) {
        try {
            DateTimeFormatter formatoUI = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fecha = LocalDate.parse(fechaUI, formatoUI);

            OffsetDateTime fechaOffset = fecha
                    .atStartOfDay()
                    .atOffset(ZoneOffset.UTC);

            return fechaOffset.toString();
            // 2026-03-03T00:00:00Z

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void programarNotificacion(String nombreCultivo, String fechaCosechaAPI) {

        try {

            LocalDate fechaCosecha = LocalDate.parse(fechaCosechaAPI);
            LocalDate fechaNotificacion = fechaCosecha.minusDays(1);

            // 🔥 Hora fija: 9 AM el día anterior
            LocalDateTime fechaObjetivo = fechaNotificacion.atTime(9, 0);

            LocalDateTime ahora = LocalDateTime.now();

            if (fechaObjetivo.isBefore(ahora)) {
                Log.d("NOTIF", "La fecha ya pasó, no se programa");
                return;
            }

            long delay = ChronoUnit.MILLIS.between(ahora, fechaObjetivo);

            Data data = new Data.Builder()
                    .putString("nombre", nombreCultivo)
                    .build();

            OneTimeWorkRequest request =
                    new OneTimeWorkRequest.Builder(NotificacionCosechaWorker.class)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(data)
                            .build();

            WorkManager.getInstance(this).enqueue(request);

            Log.d("NOTIF", "Notificación programada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void snack(String msg) {
        Snackbar.make(findViewById(android.R.id.content),
                msg, Snackbar.LENGTH_LONG).show();
    }
}
