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

import Model.Cultivo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.CultivoAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class Lista_Cultivos_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CultivoAdapter adapter;
    private List<Cultivo> listaCultivos = new ArrayList<>();
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.item_lista_cultivos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recycler_cultivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CultivoAdapter(this, listaCultivos);
        recyclerView.setAdapter(adapter);

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

                } else {
                    Toast.makeText(Lista_Cultivos_Activity.this,
                            "Error al obtener cultivos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                Toast.makeText(Lista_Cultivos_Activity.this,
                        "Error conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}