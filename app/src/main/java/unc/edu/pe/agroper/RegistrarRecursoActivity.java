package unc.edu.pe.agroper;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class RegistrarRecursoActivity extends AppCompatActivity {
    private TextInputEditText etNombre, etTipo, etUnidad, etPrecio;
    private MaterialButton btnGuardar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_recurso);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        etNombre = findViewById(R.id.et_nombre);
        etTipo = findViewById(R.id.et_tipo);
        etUnidad = findViewById(R.id.et_unidad);
        etPrecio = findViewById(R.id.et_precio);
        btnGuardar = findViewById(R.id.btn_guardar);

        btnGuardar.setOnClickListener(v -> guardarRecurso());
    }

    private void guardarRecurso() {

        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String unidad = etUnidad.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if(nombre.isEmpty() || tipo.isEmpty() || unidad.isEmpty() || precioStr.isEmpty()){
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);

        Map<String, Object> body = new HashMap<>();
        body.put("nombreInsumo", nombre);
        body.put("tipoInsumo", tipo);
        body.put("unidadMedida", unidad);
        body.put("precioEstablecido", precio);

        apiService.registrarRecurso(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    Toast.makeText(RegistrarRecursoActivity.this,
                            "Insumo registrado correctamente",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegistrarRecursoActivity.this,
                            "Error al registrar",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegistrarRecursoActivity.this,
                        "Error conexión",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}