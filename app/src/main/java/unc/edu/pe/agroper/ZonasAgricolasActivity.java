package unc.edu.pe.agroper;

import android.os.Bundle;
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

import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.ZonaAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ZonasAgricolasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ZonaAdapter adapter;
    private List<ZonaAgricola> lista = new ArrayList<>();
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zonas_agricolas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerZonas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ZonaAdapter(lista);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        cargarZonas();
    }

    private void cargarZonas() {

        apiService.obtenerZonas().enqueue(new Callback<List<ZonaAgricola>>() {

            @Override
            public void onResponse(Call<List<ZonaAgricola>> call,
                                   Response<List<ZonaAgricola>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    lista.clear();
                    lista.addAll(response.body());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ZonasAgricolasActivity.this,
                            "Error al obtener zonas",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ZonaAgricola>> call, Throwable t) {
                Toast.makeText(ZonasAgricolasActivity.this,
                        "Sin conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}