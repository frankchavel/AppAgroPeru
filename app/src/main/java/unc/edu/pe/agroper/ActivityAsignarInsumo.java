package unc.edu.pe.agroper;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Model.CultivoInsumo;
import Model.Recurso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ActivityAsignarInsumo extends AppCompatActivity {
    private Spinner spinnerInsumos;
    private TextView tvNombre, tvTipo, tvUnidad, tvPrecio, tvTotal;
    private TextInputEditText etCantidad;
    private MaterialButton btnGuardar;

    private List<Recurso> listaInsumos = new ArrayList<>();
    private Recurso recursoSeleccionado;
    private ApiService apiService;
    private int cultivoId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurso);

        spinnerInsumos = findViewById(R.id.spinner_insumos);
        tvNombre = findViewById(R.id.tv_nombre_insumo);
        tvTipo = findViewById(R.id.tv_tipo_insumo);
        tvUnidad = findViewById(R.id.tv_unidad_insumo);
        tvPrecio = findViewById(R.id.tv_precio_insumo);
        tvTotal = findViewById(R.id.tv_total);
        etCantidad = findViewById(R.id.et_cantidad);
        btnGuardar = findViewById(R.id.btn_guardar);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        cultivoId = getIntent().getIntExtra("cultivoId", 0);
        btnGuardar.setOnClickListener(v -> guardarAsignacion());




        cargarInsumos();

        spinnerInsumos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                recursoSeleccionado = listaInsumos.get(position);
                mostrarInfoInsumo();
                calcularTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularTotal();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // 🔹 CARGAR INSUMOS (Simulación)
    private void cargarInsumos() {

        apiService.obtenerRecursos().enqueue(new Callback<List<Recurso>>() {
            @Override
            public void onResponse(Call<List<Recurso>> call, Response<List<Recurso>> response) {

                if(response.isSuccessful() && response.body() != null){

                    listaInsumos.clear();
                    listaInsumos.addAll(response.body());

                    List<String> nombres = new ArrayList<>();

                    for(Recurso r : listaInsumos){
                        nombres.add(r.getNombreInsumo());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ActivityAsignarInsumo.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            nombres
                    );

                    spinnerInsumos.setAdapter(adapter);

                } else {
                    Toast.makeText(ActivityAsignarInsumo.this,
                            "No hay insumos disponibles",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recurso>> call, Throwable t) {

                Toast.makeText(ActivityAsignarInsumo.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // 🔹 MOSTRAR INFO
    private void mostrarInfoInsumo() {

        tvNombre.setText("Nombre: " + recursoSeleccionado.getNombreInsumo());
        tvTipo.setText("Tipo: " + recursoSeleccionado.getTipoInsumo());
        tvUnidad.setText("Unidad: " + recursoSeleccionado.getUnidadMedida());
        tvPrecio.setText("Precio: S/ " + recursoSeleccionado.getPrecioEstablecido());
    }

    // 🔹 CALCULAR TOTAL
    private void calcularTotal() {

        if(recursoSeleccionado == null) return;

        String cantidadStr = etCantidad.getText().toString();

        if(cantidadStr.isEmpty()){
            tvTotal.setText("Total: S/ 0.00");
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            double total = cantidad * recursoSeleccionado.getPrecioEstablecido();

            tvTotal.setText("Total: S/ " + String.format(Locale.getDefault(),"%.2f", total));

        } catch (Exception e){
            tvTotal.setText("Total: S/ 0.00");
        }
    }
    private void guardarAsignacion() {

        if(recursoSeleccionado == null){
            Toast.makeText(this, "Seleccione un insumo", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = etCantidad.getText().toString().trim();

        if(cantidadStr.isEmpty()){
            Toast.makeText(this, "Ingrese cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantidadStr);
        double total = cantidad * recursoSeleccionado.getPrecioEstablecido();

        CultivoInsumo body = new CultivoInsumo();
        body.setCultivoID(cultivoId);
        body.setInsumoID(recursoSeleccionado.getInsumoID());
        body.setCantidad(cantidad);
        body.setCostoTotal(total);  // ← antes ponías "total" en el Map, no llegaba

        apiService.asignarInsumo(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ActivityAsignarInsumo.this,
                            "Insumo asignado correctamente",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ActivityAsignarInsumo.this,
                            "Error al guardar",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ActivityAsignarInsumo.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}