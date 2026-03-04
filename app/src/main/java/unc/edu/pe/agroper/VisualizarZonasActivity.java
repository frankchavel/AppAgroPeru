package unc.edu.pe.agroper;

import android.os.Bundle;
import android.view.View;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class VisualizarZonasActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap googleMap;
    private ApiService apiService;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_zonas);

        mainView = findViewById(R.id.main);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_zonas);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        cargarZonasDesdeApi();
    }

    private void cargarZonasDesdeApi() {

        apiService.obtenerZonas().enqueue(new Callback<List<ZonaAgricola>>() {
            @Override
            public void onResponse(Call<List<ZonaAgricola>> call,
                                   Response<List<ZonaAgricola>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<ZonaAgricola> zonas = response.body();

                    if (zonas.isEmpty()) {
                        mostrarSnackbar("No hay zonas registradas 🌾");
                        return;
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (ZonaAgricola zona : zonas) {

                        LatLng latLng = new LatLng(
                                zona.getLatitud(),
                                zona.getLongitud()
                        );

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(zona.getNombreZona()) // ← Nombre visible
                                .snippet(zona.getDescripcion())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        if (marker != null) {
                            marker.showInfoWindow(); // ← Esto hace que el nombre se vea siempre
                        }

                        builder.include(latLng);
                    }

                    // Ajustar cámara para mostrar todas
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                    builder.build(),
                                    150
                            )
                    );

                } else {
                    mostrarSnackbar("Error al cargar zonas");
                }
            }

            @Override
            public void onFailure(Call<List<ZonaAgricola>> call, Throwable t) {
                mostrarSnackbar("Sin conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar.make(mainView, mensaje, Snackbar.LENGTH_LONG).show();
    }
}