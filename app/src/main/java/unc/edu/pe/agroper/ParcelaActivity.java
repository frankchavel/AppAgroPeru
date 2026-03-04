package unc.edu.pe.agroper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Cultivo;
import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.CultivoDashboardAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.Clima.WeatherApiService;
import unc.edu.pe.agroper.Service.Clima.WeatherRequest;
import unc.edu.pe.agroper.Service.Clima.WeatherResponse;
import unc.edu.pe.agroper.Service.Clima.WeatherRetrofitClient;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ParcelaActivity extends BaseActivity {
    private TextView tvTemp, tvDesc, tvHumedad, tvLluvia, tvViento;
    private ProgressBar pbClima;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 2001;

    private ApiService apiService;
    private WeatherApiService weatherService;

    private MaterialCardView cardHelada, cardLluvia;
    private TextView tvAlertaHelada, tvAlertaLluvia;
    private RecyclerView rvCultivos;
    private CultivoDashboardAdapter adapter;
    private List<Cultivo> listaCultivos = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parcela);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();

        // Botón: Ver Mapa → ZonaAgricolaActivity
        findViewById(R.id.btn_ver_mapa).setOnClickListener(v -> {
            Intent intent = new Intent(this, VisualizarZonasActivity.class);
            startActivity(intent);
        });
        // Botón: Ver Precio → ZonaAgricolaActivity
        findViewById(R.id.btn_precios).setOnClickListener(v -> {
            Intent intent = new Intent(this, PreciosActivity.class);
            startActivity(intent);
        });

        // Botón: Ver Precio → ZonaAgricolaActivity
        findViewById(R.id.btn_nuevo_cultivo).setOnClickListener(v -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                Intent intent = new Intent(this, MisCultivosActivity.class);
                intent.putExtra("v_usu", user.getEmail());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }
        });

        tvTemp = findViewById(R.id.tv_temp_actual);
        tvDesc = findViewById(R.id.tv_desc_clima);
        tvHumedad = findViewById(R.id.tv_humedad);
        tvLluvia = findViewById(R.id.tv_lluvia);
        tvViento = findViewById(R.id.tv_viento);
        pbClima = findViewById(R.id.pb_clima);
        cardHelada = findViewById(R.id.card_alerta_helada);
        cardLluvia = findViewById(R.id.card_alerta_lluvia);

        tvAlertaHelada = findViewById(R.id.tv_alerta_helada);
        tvAlertaLluvia = findViewById(R.id.tv_alerta_lluvia);
        rvCultivos = findViewById(R.id.rv_cultivos);
        rvCultivos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CultivoDashboardAdapter(listaCultivos);
        rvCultivos.setAdapter(adapter);

        // Ocultarlas por defecto
        cardHelada.setVisibility(View.GONE);
        cardLluvia.setVisibility(View.GONE);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        weatherService = WeatherRetrofitClient
                .getClient("AIzaSyDHVIIqO-NU4dwS2M2a4Rq4xTLo6gL80g8")
                .create(WeatherApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerUbicacionActualYClima();
        cargarMisCultivos();

    }
    private void obtenerNombreLugar(double lat, double lng) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                String ciudad = address.getLocality();
                String pais = address.getCountryName();

                TextView tvUbicacion = findViewById(R.id.tv_ubicacion);

                tvUbicacion.setText(ciudad + ", " + pais);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void obtenerUbicacionActualYClima() {

        pbClima.setVisibility(View.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                // 🔹 Obtener nombre real del lugar
                obtenerNombreLugar(lat, lng);

                // 🔹 Obtener clima real
                obtenerClima(lat, lng);

            } else {
                pbClima.setVisibility(View.GONE);
                Toast.makeText(this,
                        "No se pudo obtener ubicación actual",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void cargarMisCultivos() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        apiService.obtenerCultivosPorUsuario(user.getEmail())
                .enqueue(new Callback<List<Cultivo>>() {

                    @Override
                    public void onResponse(Call<List<Cultivo>> call,
                                           Response<List<Cultivo>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            listaCultivos.clear();
                            listaCultivos.addAll(response.body());

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                        Log.e("CULTIVOS", "Error: " + t.getMessage());
                    }
                });
    }

    private void obtenerClima(double lat, double lng) {

        pbClima.setVisibility(View.VISIBLE);

        weatherService.getWeather(lat, lng)
                .enqueue(new Callback<WeatherResponse>() {

                    @Override
                    public void onResponse(Call<WeatherResponse> call,
                                           Response<WeatherResponse> response) {

                        pbClima.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {

                            WeatherResponse clima = response.body();

                            tvTemp.setText(Math.round(clima.temperature.degrees) + "°");
                            tvDesc.setText("Clima actual");
                            tvHumedad.setText(clima.humidity + "%");
                            tvLluvia.setText(clima.precipitationProbability + "%");
                            tvViento.setText(
                                    Math.round(clima.wind.speed.value) + " km/h"
                            );
                            evaluarAlertas(clima);

                        } else {
                            Log.e("CLIMA_ERROR", "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        pbClima.setVisibility(View.GONE);
                        Log.e("CLIMA_ERROR", t.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                obtenerUbicacionActualYClima();

            } else {

                pbClima.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Permiso de ubicación denegado",
                        Toast.LENGTH_LONG).show();

            }
        }
    }
    // =============================
    // ALERTAS
    // =============================

    private void evaluarAlertas(WeatherResponse clima) {

        cardLluvia.setVisibility(View.VISIBLE);
        tvAlertaLluvia.setText("🌧 PRUEBA LLUVIA 90%");
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarMisCultivos();
    }
}