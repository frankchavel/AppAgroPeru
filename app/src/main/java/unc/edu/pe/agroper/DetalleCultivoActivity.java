package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Cultivo;
import Model.CultivoInsumo;
import Model.Recurso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.CultivoInsumoAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class DetalleCultivoActivity extends AppCompatActivity {
    private TextView tvTitulo, tvFecha;
    private MaterialButton btnAsignar;
    private RecyclerView recyclerInsumos;
    private ApiService apiService;
    private TextView tvTotalCostos;
    private int cultivoId;
    private TextView tvTotalInsumos, tvManoObra, tvMaquinaria, tvTotalProduccion;

    // Variables fijas (pueden venir luego desde BD)
    private double costoManoObra = 0;
    private double costoMaquinaria = 0;

    private List<CultivoInsumo> insumosAsignados = new ArrayList<>();
    private List<Recurso> todosLosInsumos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_detalle_cultivo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getClient().create(ApiService.class);
        cultivoId = getIntent().getIntExtra("cultivoId", 0);

        tvTitulo = findViewById(R.id.tv_titulo_cultivo);
        tvFecha = findViewById(R.id.tv_fecha_siembra);
        btnAsignar = findViewById(R.id.btn_asignar_insumo);
        recyclerInsumos = findViewById(R.id.recycler_insumos);
        tvTotalCostos = findViewById(R.id.tv_total_costos);
        tvTotalInsumos = findViewById(R.id.tv_total_insumos);
        tvManoObra = findViewById(R.id.tv_mano_obra);
        tvMaquinaria = findViewById(R.id.tv_maquinaria);
        tvTotalProduccion = findViewById(R.id.tv_total_produccion);
        recyclerInsumos.setLayoutManager(new LinearLayoutManager(this));

        cargarDetalle();
        cargarInsumosAsignados();

        btnAsignar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityAsignarInsumo.class);
            intent.putExtra("cultivoId", cultivoId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarga al volver de asignar insumo
        cargarInsumosAsignados();
    }

    private void cargarDetalle() {
        apiService.obtenerCultivos().enqueue(new Callback<List<Cultivo>>() {
            @Override
            public void onResponse(Call<List<Cultivo>> call,
                                   Response<List<Cultivo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Cultivo c : response.body()) {
                        if (c.getCultivoID() == cultivoId) {
                            tvTitulo.setText(c.getNombreCultivo());
                            tvFecha.setText("Sembrado el " + formatearFecha(c.getFechaSiembra()));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                Toast.makeText(DetalleCultivoActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarInsumosAsignados() {
        // Primero carga todos los recursos disponibles
        apiService.obtenerRecursos().enqueue(new Callback<List<Recurso>>() {
            @Override
            public void onResponse(Call<List<Recurso>> call,
                                   Response<List<Recurso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todosLosInsumos = response.body();
                    // Luego carga los asignados
                    cargarCultivoInsumos();
                }
            }

            @Override
            public void onFailure(Call<List<Recurso>> call, Throwable t) {
                Toast.makeText(DetalleCultivoActivity.this,
                        "Error cargando recursos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCultivoInsumos() {
        apiService.obtenerCultivoInsumos().enqueue(new Callback<List<CultivoInsumo>>() {
            @Override
            public void onResponse(Call<List<CultivoInsumo>> call,
                                   Response<List<CultivoInsumo>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    insumosAsignados.clear();
                    for (CultivoInsumo ci : response.body()) {
                        if (ci.getCultivoID() == cultivoId) {
                            insumosAsignados.add(ci);
                        }
                    }

                    CultivoInsumoAdapter adapter = new CultivoInsumoAdapter(
                            DetalleCultivoActivity.this,
                            insumosAsignados,
                            todosLosInsumos
                    );
                    recyclerInsumos.setAdapter(adapter);
                    calcularTotalProduccion();
                }
            }

            @Override
            public void onFailure(Call<List<CultivoInsumo>> call, Throwable t) {
                Toast.makeText(DetalleCultivoActivity.this,
                        "Error cargando insumos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatearFecha(String fechaISO) {
        try {
            SimpleDateFormat entrada = new SimpleDateFormat(
                    fechaISO.contains("T") ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd",
                    Locale.getDefault()
            );
            SimpleDateFormat salida = new SimpleDateFormat(
                    "dd 'de' MMMM 'de' yyyy", new Locale("es", "PE")
            );
            Date date = entrada.parse(fechaISO);
            return salida.format(date);
        } catch (Exception e) {
            return fechaISO;
        }
    }
    private void calcularTotalProduccion() {

        double totalInsumos = 0;

        for (CultivoInsumo ci : insumosAsignados) {

            for (Recurso r : todosLosInsumos) {

                if (r.getInsumoID() == ci.getInsumoID()) {

                    double precio = r.getPrecioEstablecido();
                    double cantidad = ci.getCantidad();

                    double subtotal = precio * cantidad;

                    totalInsumos += subtotal;
                    break;
                }
            }
        }

        // 🔹 Ejemplo de cálculo de mano de obra
        // Puedes cambiar lógica según tus necesidades
        costoManoObra = totalInsumos * 0.15;   // 15% del costo insumos

        // 🔹 Ejemplo de maquinaria
        costoMaquinaria = totalInsumos * 0.10; // 10% del costo insumos

        double totalProduccion = totalInsumos + costoManoObra + costoMaquinaria;

        tvTotalInsumos.setText("Total Insumos: S/ " +
                String.format(Locale.getDefault(), "%.2f", totalInsumos));

        tvManoObra.setText("Mano de Obra: S/ " +
                String.format(Locale.getDefault(), "%.2f", costoManoObra));

        tvMaquinaria.setText("Maquinaria: S/ " +
                String.format(Locale.getDefault(), "%.2f", costoMaquinaria));

        tvTotalProduccion.setText("TOTAL PRODUCCIÓN: S/ " +
                String.format(Locale.getDefault(), "%.2f", totalProduccion));
    }
}