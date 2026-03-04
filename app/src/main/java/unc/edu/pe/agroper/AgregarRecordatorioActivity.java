package unc.edu.pe.agroper;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Model.Cultivo;
import Model.Recordatorio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;
import unc.edu.pe.agroper.databinding.ActivityAgregarRecordatorioBinding;

public class AgregarRecordatorioActivity extends AppCompatActivity {

    private ActivityAgregarRecordatorioBinding binding;
    private String actividadSeleccionada = "";
    private Calendar calendario = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    // Lista para almacenar los cultivos de la API
    private List<Cultivo> listaCultivos = new ArrayList<>();
    private List<String> nombresCultivos = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 1. Inicializar View Binding
        binding = ActivityAgregarRecordatorioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar ApiService
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Configurar componentes
        setupActividades();
        setupDateTimePickers();
        setupSpinner();

        // Mostrar fecha y hora actual por defecto
        actualizarTextoFechaHora();

        // Solicitar permisos de notificación para Android 13+
        solicitarPermisosNotificacion();

        // 3. Botones de acción
        binding.btnCancelar.setOnClickListener(v -> finish());
        binding.btnCrearTarea.setOnClickListener(v -> guardarTarea());
    }

    private void setupActividades() {
        binding.cardSiembra.setOnClickListener(v -> seleccionarActividad("Siembra", binding.cardSiembra));
        binding.cardRiego.setOnClickListener(v -> seleccionarActividad("Riego", binding.cardRiego));
        binding.cardFertilizacion.setOnClickListener(v -> seleccionarActividad("Fertilización", binding.cardFertilizacion));
        binding.cardCosecha.setOnClickListener(v -> seleccionarActividad("Cosecha", binding.cardCosecha));
    }

    private void seleccionarActividad(String actividad, MaterialCardView card) {
        binding.cardSiembra.setStrokeWidth(0);
        binding.cardRiego.setStrokeWidth(0);
        binding.cardFertilizacion.setStrokeWidth(0);
        binding.cardCosecha.setStrokeWidth(0);

        card.setStrokeColor(Color.parseColor("#39FF14"));
        card.setStrokeWidth(4);
        actividadSeleccionada = actividad;
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                nombresCultivos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCultivo.setAdapter(spinnerAdapter);
        cargarCultivos();
    }

    private void cargarCultivos() {
        apiService.obtenerCultivos().enqueue(new Callback<List<Cultivo>>() {
            @Override
            public void onResponse(Call<List<Cultivo>> call, Response<List<Cultivo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCultivos.clear();
                    listaCultivos.addAll(response.body());

                    nombresCultivos.clear();
                    for (Cultivo cultivo : listaCultivos) {
                        String nombreCultivo = cultivo.getNombreCultivo();
                        nombresCultivos.add(nombreCultivo);
                    }

                    spinnerAdapter.notifyDataSetChanged();

                    if (nombresCultivos.isEmpty()) {
                        Toast.makeText(AgregarRecordatorioActivity.this,
                                "No hay cultivos disponibles", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AgregarRecordatorioActivity.this,
                            "Error al cargar cultivos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                Toast.makeText(AgregarRecordatorioActivity.this,
                        "Error de conexión al cargar cultivos: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                nombresCultivos.clear();
                nombresCultivos.add("Error al cargar cultivos");
                spinnerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupDateTimePickers() {
        // Click en el texto de fecha para abrir DatePickerDialog
        binding.tvFechaProgramada.setOnClickListener(v -> mostrarDatePicker());

        // Click en el icono de fecha para abrir DatePickerDialog
        binding.icFecha.setOnClickListener(v -> mostrarDatePicker());

        // Click en el texto de hora para abrir TimePickerDialog
        binding.tvHoraProgramada.setOnClickListener(v -> mostrarTimePicker());

        // Click en el icono de hora para abrir TimePickerDialog (asumiendo que tienes un icono de hora)
        // Si no tienes un icono de hora específico, puedes omitir esta línea o agregar un nuevo ImageView
        // binding.icHora.setOnClickListener(v -> mostrarTimePicker());
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, month);
            calendario.set(Calendar.DAY_OF_MONTH, day);
            actualizarTextoFechaHora();
        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendario.set(Calendar.MINUTE, minute);
            actualizarTextoFechaHora();
        }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show();
    }

    private void actualizarTextoFechaHora() {
        binding.tvFechaProgramada.setText(dateFormat.format(calendario.getTime()));
    }

    private void guardarTarea() {
        if (actividadSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una actividad", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionSeleccionada = binding.spinnerCultivo.getSelectedItemPosition();
        if (posicionSeleccionada < 0 || posicionSeleccionada >= listaCultivos.size()) {
            Toast.makeText(this, "Selecciona un cultivo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Cultivo cultivoSeleccionado = listaCultivos.get(posicionSeleccionada);

        // Verificar que la fecha programada no sea en el pasado
        if (calendario.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(this, "La fecha programada no puede ser en el pasado", Toast.LENGTH_SHORT).show();
            return;
        }

        Recordatorio r = new Recordatorio();
        r.setCultivoID(cultivoSeleccionado.getCultivoID());
        r.setActividad(actividadSeleccionada);
        r.setFechaProgramada(dateFormat.format(calendario.getTime()));
        r.setNotificado(binding.switchNotificacion.isChecked());
        r.setNotas(binding.etNotas.getText().toString());
        r.setEstado("PENDIENTE");

        // Programar notificación para la fecha y hora seleccionada
        if (r.isNotificado()) {
            programarNotificacion(r);
        }

        Toast.makeText(this, "Tarea guardada correctamente para el cultivo: " +
                        cultivoSeleccionado.getNombreCultivo() + " el " + dateFormat.format(calendario.getTime()),
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void programarNotificacion(Recordatorio r) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificacionReceiver.class);

        String mensaje = "Recordatorio: " + r.getActividad() + " para hoy";
        intent.putExtra("detalle", mensaje);
        intent.putExtra("recordatorioId", r.getRecordatorioID());

        int requestCode = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Usar la fecha y hora seleccionada por el usuario
        long tiempoProgramado = calendario.getTimeInMillis();

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoProgramado, pendingIntent);
                    Toast.makeText(this, "Notificación programada para " + dateFormat.format(calendario.getTime()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoProgramado, pendingIntent);
                    Toast.makeText(this, "Notificación programada (modo estándar) para " +
                            dateFormat.format(calendario.getTime()), Toast.LENGTH_SHORT).show();
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoProgramado, pendingIntent);
                Toast.makeText(this, "Notificación programada para " + dateFormat.format(calendario.getTime()),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void solicitarPermisosNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
}