package unc.edu.pe.agroper;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Model.Recurso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ActivityRecurso extends AppCompatActivity {
    private TextInputEditText etNombre, etPrecio, etFecha;
    private AutoCompleteTextView etTipo, etUnidad;
    private MaterialButton btnGuardar, btnCancelar;
    private ImageView btnBack;

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recurso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        initViews();
        configurarDropdowns();
        configurarFecha();
        configurarBotones();
    }
    private void initViews() {
        etNombre = findViewById(R.id.et_nombre_insumo);
        etTipo = findViewById(R.id.et_tipo_insumo);
        etUnidad = findViewById(R.id.et_unidad_medida);
        etPrecio = findViewById(R.id.et_precio);
        etFecha = findViewById(R.id.et_fecha_registro);

        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnBack = findViewById(R.id.btn_back);
    }

    private void configurarDropdowns() {

        String[] tipos = {"Fertilizante", "Pesticida", "Herbicida", "Semilla", "Abono"};
        String[] unidades = {"Kg", "Litros", "Unidad", "Saco", "Tonelada"};

        ArrayAdapter<String> adapterTipo =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tipos);
        etTipo.setAdapter(adapterTipo);

        ArrayAdapter<String> adapterUnidad =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, unidades);
        etUnidad.setAdapter(adapterUnidad);
    }

    private void configurarFecha() {
        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    ActivityRecurso.this,
                    (view, year, month, dayOfMonth) -> {

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        SimpleDateFormat sdf =
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                        etFecha.setText(sdf.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.show();
        });
    }

    private void configurarBotones() {

        btnBack.setOnClickListener(v -> finish());

        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> guardarInsumo());
    }

    private void guardarInsumo() {

        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String unidad = etUnidad.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (nombre.isEmpty() || tipo.isEmpty() || unidad.isEmpty()
                || precioStr.isEmpty() || fecha.isEmpty()) {

            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);

        Recurso recurso = new Recurso();
        recurso.setNombreInsumo(nombre);
        recurso.setTipoInsumo(tipo);
        recurso.setUnidadMedida(unidad);
        recurso.setPrecioEstablecido(precio);
        recurso.setFechaRegistro(fecha);

        apiService.crearInsumo(recurso).enqueue(new Callback<Recurso>() {
            @Override
            public void onResponse(Call<Recurso> call, Response<Recurso> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ActivityRecurso.this,
                            "Insumo guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityRecurso.this,
                            "Error al guardar",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Recurso> call, Throwable t) {
                Toast.makeText(ActivityRecurso.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}