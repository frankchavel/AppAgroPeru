package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        apiService.obtenerCultivosPorUsuario(user.getEmail())
                .enqueue(new Callback<List<Cultivo>>() {

                    @Override
                    public void onResponse(Call<List<Cultivo>> call, Response<List<Cultivo>> response) {
                        Log.d("DEBUG", "Código: " + response.code());

                        if (response.body() != null) {
                            Log.d("DEBUG", "Cantidad recibida: " + response.body().size());
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            adapter.actualizarLista(response.body());
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.actualizarLista(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                        Toast.makeText(Lista_Cultivos_Activity.this,
                                "Error conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        Log.d("CORREO_ACTUAL", user.getEmail());
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarCultivos(); // 🔥 Esto fuerza recarga cada vez que vuelves
    }
}