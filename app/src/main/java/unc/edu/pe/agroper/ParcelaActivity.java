package unc.edu.pe.agroper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import Model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Adapter.CultivoDashboardAdapter;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.Clima.WeatherApiService;
import unc.edu.pe.agroper.Service.Clima.WeatherResponse;
import unc.edu.pe.agroper.Service.Clima.WeatherRetrofitClient;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ParcelaActivity extends BaseActivity {
    // 🔥 Añadida la variable tvUbicacion aquí
    private TextView tvTemp, tvDesc, tvHumedad, tvLluvia, tvViento, tvNombreUsuario, tvUbicacion;
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

        // Configurar menú inferior
        setupBottomNavigation();
        selectBottomNavigationItem();

        inicializarVistas();
        configurarServicios();
        configurarListeners();

        obtenerUbicacionActualYClima();
        obtenerNombreDeAPI();
        cargarMisCultivos();
    }

    private void inicializarVistas() {
        tvNombreUsuario = findViewById(R.id.tv_nombre_usuario);
        tvTemp = findViewById(R.id.tv_temp_actual);
        tvDesc = findViewById(R.id.tv_desc_clima);
        tvHumedad = findViewById(R.id.tv_humedad);
        tvLluvia = findViewById(R.id.tv_lluvia);
        tvViento = findViewById(R.id.tv_viento);
        tvUbicacion = findViewById(R.id.tv_ubicacion); // 🔥 Conectada la vista de ubicación
        pbClima = findViewById(R.id.pb_clima);

        cardHelada = findViewById(R.id.card_alerta_helada);
        cardLluvia = findViewById(R.id.card_alerta_lluvia);
        tvAlertaHelada = findViewById(R.id.tv_alerta_helada);
        tvAlertaLluvia = findViewById(R.id.tv_alerta_lluvia);

        rvCultivos = findViewById(R.id.rv_cultivos);
        rvCultivos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CultivoDashboardAdapter(listaCultivos);
        rvCultivos.setAdapter(adapter);

        // Ocultar alertas por defecto
        cardHelada.setVisibility(View.GONE);
        cardLluvia.setVisibility(View.GONE);
    }

    private void configurarServicios() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        weatherService = WeatherRetrofitClient
                .getClient("AIzaSyDHVIIqO-NU4dwS2M2a4Rq4xTLo6gL80g8")
                .create(WeatherApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void configurarListeners() {

        // ==========================================
        // 🔥 NUEVO: Botón Avatar de Perfil -> PerfilActivity
        // ==========================================
        findViewById(R.id.cv_perfil).setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });

        findViewById(R.id.btn_recordatorios).setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarioActivity.class));
        });

        findViewById(R.id.btn_ver_mapa).setOnClickListener(v -> {
            startActivity(new Intent(this, VisualizarZonasActivity.class));
        });

        findViewById(R.id.btn_precios).setOnClickListener(v -> {
            startActivity(new Intent(this, PreciosActivity.class));
        });

        findViewById(R.id.btn_nuevo_cultivo).setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroCultivoActivity.class));
        });
    }

    // 🔥 MÉTODO PARA JALAR EL NOMBRE DESDE TU API WEB
    private void obtenerNombreDeAPI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        apiService.obtenerUsuarioPorEmail(user.getEmail()).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombreReal = response.body().getNombresCompletos();
                    if (nombreReal != null && !nombreReal.isEmpty()) {
                        tvNombreUsuario.setText(nombreReal);
                    }
                } else {
                    String nombreRespaldo = user.getEmail().split("@")[0];
                    tvNombreUsuario.setText(nombreRespaldo.substring(0, 1).toUpperCase() + nombreRespaldo.substring(1));
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                String nombreRespaldo = user.getEmail().split("@")[0];
                tvNombreUsuario.setText(nombreRespaldo.substring(0, 1).toUpperCase() + nombreRespaldo.substring(1));
                Log.e("API_NAME", "Error al obtener nombre: " + t.getMessage());
            }
        });
    }

    private void obtenerNombreLugar(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                tvUbicacion.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void obtenerUbicacionActualYClima() {
        pbClima.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
            if (location != null) {
                obtenerNombreLugar(location.getLatitude(), location.getLongitude());
                obtenerClima(location.getLatitude(), location.getLongitude());
            } else {
                pbClima.setVisibility(View.GONE);
            }
        });
    }

    private void cargarMisCultivos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        apiService.obtenerCultivosPorUsuario(user.getEmail())
                .enqueue(new Callback<List<Cultivo>>() {

                    @Override
                    public void onResponse(Call<List<Cultivo>> call,
                                           Response<List<Cultivo>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            listaCultivos.clear();
                            listaCultivos.addAll(response.body());

                            adapter.notifyDataSetChanged();

                        } else {
                            Log.e("CULTIVOS", "Respuesta no exitosa: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Cultivo>> call, Throwable t) {
                        Log.e("CULTIVOS", "Error: " + t.getMessage());
                    }
                });
    }

    private void obtenerClima(double lat, double lng) {
        weatherService.getWeather(lat, lng).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                pbClima.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse clima = response.body();
                    tvTemp.setText(Math.round(clima.temperature.degrees) + "°");
                    tvDesc.setText("Clima actual");
                    tvHumedad.setText(clima.humidity + "%");
                    tvLluvia.setText(clima.precipitationProbability + "%");
                    tvViento.setText(Math.round(clima.wind.speed.value) + " km/h");
                    evaluarAlertas(clima);
                }
            }
            @Override public void onFailure(Call<WeatherResponse> call, Throwable t) { pbClima.setVisibility(View.GONE); }
        });
    }

    private void evaluarAlertas(WeatherResponse clima) {
        if (clima.precipitationProbability > 70) {
            cardLluvia.setVisibility(View.VISIBLE);
            tvAlertaLluvia.setText("🌧 ALERTA: Alta prob. de lluvia (" + clima.precipitationProbability + "%)");
        }
        if (clima.temperature.degrees < 5) {
            cardHelada.setVisibility(View.VISIBLE);
            tvAlertaHelada.setText("❄️ ALERTA: Riesgo de helada (" + Math.round(clima.temperature.degrees) + "°)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActualYClima();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMisCultivos();
        obtenerNombreDeAPI();
    }
}