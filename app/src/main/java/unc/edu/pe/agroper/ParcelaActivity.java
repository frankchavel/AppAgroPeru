package unc.edu.pe.agroper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import unc.edu.pe.agroper.Service.Clima.WeatherApiService;
import unc.edu.pe.agroper.Service.Clima.WeatherResponse;
import unc.edu.pe.agroper.Service.Clima.WeatherRetrofitClient;
import unc.edu.pe.agroper.databinding.ActivityParcelaBinding;

public class ParcelaActivity extends BaseActivity {

    private ActivityParcelaBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 2001;
    private WeatherApiService weatherService;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityParcelaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar servicios
        weatherService = WeatherRetrofitClient
                .getClient("AIzaSyDHVIIqO-NU4dwS2M2a4Rq4xTLo6gL80g8")
                .create(WeatherApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar listeners
        configurarListeners();

        // Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();

        // Obtener ubicación y clima
        solicitarPermisoUbicacion();
    }

    private void configurarListeners() {
        // Botón Ver Mapa en alerta
        binding.btnVerMapa.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocalizacionCultivoActivity.class);
            startActivity(intent);
        });

        // Card de Precios
        binding.btnPrecios.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreciosActivity.class);
            startActivity(intent);
        });

        // Card de Recordatorios/Tareas
        binding.btnRecordatorios.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarioActivity.class);
            startActivity(intent);
        });

        // FAB Nuevo Cultivo
        binding.btnNuevoCultivo.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(this, MisCultivosActivity.class);
                intent.putExtra("v_usu", user.getEmail());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }
        });

        // Avatar perfil
        binding.cvPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
        });
    }

    private void solicitarPermisoUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            obtenerUbicacionPrecisa();
        }
    }

    private void obtenerUbicacionPrecisa() {
        binding.pbClima.setVisibility(View.VISIBLE);

        // Crear LocationRequest para mayor precisión
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1); // Solo una actualización
        locationRequest.setInterval(0);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    // En caso falla la precisión alta, intentar con la última ubicación conocida
                    obtenerUltimaUbicacionConocida();
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Mostrar coordenadas en log para depuración
                        Log.d("UBICACION", "Lat: " + lat + ", Lng: " + lng);

                        obtenerNombreLugar(lat, lng);
                        obtenerClima(lat, lng);
                        break;
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, getMainLooper());
        }
    }

    private void obtenerUltimaUbicacionConocida() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                Log.d("UBICACION", "Usando última ubicación conocida: " + lat + ", " + lng);

                obtenerNombreLugar(lat, lng);
                obtenerClima(lat, lng);
            } else {
                binding.pbClima.setVisibility(View.GONE);
                Toast.makeText(this,
                        "No se pudo obtener ubicación. Verifica que el GPS esté activado.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void obtenerNombreLugar(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Construir ubicación legible
                String ubicacion = "";

                // Priorizar localidad (ciudad/pueblo)
                if (address.getLocality() != null) {
                    ubicacion = address.getLocality();
                }
                // Si no hay localidad, usar admin área (departamento/región)
                else if (address.getAdminArea() != null) {
                    ubicacion = address.getAdminArea();
                }
                // Si no hay admin area, usar sub admin area
                else if (address.getSubAdminArea() != null) {
                    ubicacion = address.getSubAdminArea();
                }

                // Agregar país si está disponible
                if (address.getCountryName() != null) {
                    if (!ubicacion.isEmpty()) {
                        ubicacion += ", ";
                    }
                    ubicacion += address.getCountryName();
                }

                // Si no se pudo obtener ninguna ubicación legible, usar coordenadas
                if (ubicacion.isEmpty()) {
                    ubicacion = String.format(Locale.US, "%.2f, %.2f", lat, lng);
                }

                Log.d("UBICACION", "Ubicación encontrada: " + ubicacion);

                String finalUbicacion = ubicacion;
                runOnUiThread(() -> binding.tvUbicacionDetalle.setText(finalUbicacion));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("UBICACION", "Error de geocoder: " + e.getMessage());
            runOnUiThread(() ->
                    binding.tvUbicacionDetalle.setText(String.format(Locale.US, "%.2f, %.2f", lat, lng))
            );
        }
    }

    private void obtenerClima(double lat, double lng) {
        binding.pbClima.setVisibility(View.VISIBLE);

        weatherService.getWeather(lat, lng)
                .enqueue(new retrofit2.Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<WeatherResponse> call,
                                           retrofit2.Response<WeatherResponse> response) {
                        binding.pbClima.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse clima = response.body();

                            // ✅ DATOS REALES DE LA API
                            binding.tvTempActual.setText(Math.round(clima.temperature.degrees) + "°");

                            // Descripción basada en probabilidad de lluvia
                            String descripcion = generarDescripcionClima(
                                    clima.precipitationProbability
                            );
                            binding.tvDescClima.setText(descripcion);

                            // ✅ HUMEDAD REAL
                            binding.tvHumedad.setText(clima.humidity + "%");

                            // ✅ PROBABILIDAD LLUVIA REAL
                            binding.tvLluvia.setText(clima.precipitationProbability + "%");

                            // ✅ VIENTO REAL
                            binding.tvViento.setText(
                                    Math.round(clima.wind.speed.value) + " km/h"
                            );

                            Log.d("CLIMA_REAL", "========== DATOS CLIMA ==========");
                            Log.d("CLIMA_REAL", "Temperatura: " + clima.temperature.degrees + "°");
                            Log.d("CLIMA_REAL", "Humedad: " + clima.humidity + "%");
                            Log.d("CLIMA_REAL", "Prob. Lluvia: " + clima.precipitationProbability + "%");
                            Log.d("CLIMA_REAL", "Viento: " + clima.wind.speed.value + " km/h");
                            Log.d("CLIMA_REAL", "==================================");

                        } else {
                            Log.e("CLIMA_ERROR", "Error HTTP: " + response.code());
                            Toast.makeText(ParcelaActivity.this,
                                    "Error al obtener clima",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {
                        binding.pbClima.setVisibility(View.GONE);
                        Log.e("CLIMA_ERROR", "Error de conexión: " + t.getMessage());
                        Toast.makeText(ParcelaActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generarDescripcionClima(int probLluvia) {
        if (probLluvia >= 70) {
            return "Lluvioso";
        } else if (probLluvia >= 40) {
            return "Probabilidad de lluvia";
        } else if (probLluvia >= 10) {
            return "Parcialmente nublado";
        } else {
            return "Despejado";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionPrecisa();
            } else {
                binding.pbClima.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Permiso de ubicación denegado. No se mostrará el clima.",
                        Toast.LENGTH_LONG).show();

                // Mostrar ubicación por defecto
                binding.tvUbicacionDetalle.setText("Ubicación no disponible");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar callbacks para evitar memory leaks
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}