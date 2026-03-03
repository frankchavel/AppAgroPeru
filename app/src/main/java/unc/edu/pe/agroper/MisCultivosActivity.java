package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Model.Cultivo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.CultivoAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class MisCultivosActivity extends AppCompatActivity {
    RecyclerView rvMisCultivos;
    LinearLayout layoutVacio;
    TextView tvTotalCultivos, tvTotalHectareas;
    CultivoAdapter adapter;
    List<Cultivo> listaCultivos = new ArrayList<>();
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_cultivos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.fab_nuevo_cultivo).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroCultivoActivity.class);
            startActivity(intent);
        });
        rvMisCultivos = findViewById(R.id.rv_mis_cultivos);
        layoutVacio = findViewById(R.id.layout_vacio);
        tvTotalCultivos = findViewById(R.id.tv_total_cultivos);
        tvTotalHectareas = findViewById(R.id.tv_total_hectareas);

        rvMisCultivos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CultivoAdapter(this, listaCultivos);
        rvMisCultivos.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        cargarCultivos();

    }
    private void cargarCultivos() {

        apiService.obtenerCultivos().enqueue(new Callback<List<Cultivo>>() {

            @Override
            public void onResponse(Call<List<Cultivo>> call, Response<List<Cultivo>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaCultivos.clear();
                    listaCultivos.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    actualizarResumen();
                    verificarEstadoVacio();

                } else {

                    Toast.makeText(MisCultivosActivity.this,
                            "Error al obtener cultivos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cultivo>> call, Throwable t) {

                Toast.makeText(MisCultivosActivity.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarResumen() {

        tvTotalCultivos.setText(String.valueOf(listaCultivos.size()));

        double totalHectareas = 0;

        for (Cultivo c : listaCultivos) {
            totalHectareas += c.getAreaCultivo();
        }

        tvTotalHectareas.setText(String.valueOf(totalHectareas));
    }
    private void verificarEstadoVacio() {

        if (listaCultivos.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            rvMisCultivos.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            rvMisCultivos.setVisibility(View.VISIBLE);
        }
    }





}