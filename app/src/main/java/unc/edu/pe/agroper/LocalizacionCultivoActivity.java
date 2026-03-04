package unc.edu.pe.agroper;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class LocalizacionCultivoActivity extends BaseActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_CODE = 2001;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Double latitudSeleccionada = null;
    private Double longitudSeleccionada = null;

    private TextInputLayout tilNombreZona, tilDescripcion;
    private TextInputEditText etNombreZona, etDescripcion, etLatitud, etLongitud;
    private TextView tvCoordsStatus;
    private MaterialButton btnGuardar;
    private FloatingActionButton fabLocation;
    private Chip chipUbicacionActual;
    private MaterialCardView cardCoordsStatus;
    private View mainView;

    private ApiService apiService;

    // ── Tipo de mapa actual ───────────────────────────────────────────
    private int tipoMapaActual = GoogleMap.MAP_TYPE_SATELLITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_localizacion_cultivo);

        mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();

        findViewById(R.id.btn_ver_zonas).setOnClickListener(v -> {
            Intent intent = new Intent(this, ZonasAgricolasActivity.class);
            startActivity(intent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        inicializarVistas();
        inicializarMapa();
        configurarListeners();
    }

    // ── Vistas ────────────────────────────────────────────────────────
    private void inicializarVistas() {
        tilNombreZona = findViewById(R.id.til_nombre_zona);
        tilDescripcion = findViewById(R.id.til_descripcion);
        etNombreZona = findViewById(R.id.et_nombre_zona);
        etDescripcion = findViewById(R.id.et_descripcion);
        etLatitud = findViewById(R.id.et_latitud);
        etLongitud = findViewById(R.id.et_longitud);
        tvCoordsStatus = findViewById(R.id.tv_coords_status);
        btnGuardar = findViewById(R.id.btn_guardar);
        fabLocation = findViewById(R.id.fab_location);
        chipUbicacionActual = findViewById(R.id.chip_ubicacion_actual);
        cardCoordsStatus = findViewById(R.id.card_coords_status);
    }

    private void configurarListeners() {
        btnGuardar.setOnClickListener(v -> guardarZona());

        // FAB y chip hacen lo mismo: ir a ubicación actual
        fabLocation.setOnClickListener(v -> {
            animarFab();
            obtenerUbicacionActual();
        });
        chipUbicacionActual.setOnClickListener(v -> {
            animarFab();
            obtenerUbicacionActual();
        });

        // Limpiar error al escribir
        etNombreZona.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {
            }

            public void onTextChanged(CharSequence s, int i, int b, int c) {
                tilNombreZona.setError(null);
            }

            public void afterTextChanged(android.text.Editable s) {
            }
        });
        etDescripcion.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {
            }

            public void onTextChanged(CharSequence s, int i, int b, int c) {
                tilDescripcion.setError(null);
            }

            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // Botón volver
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // ── Mapa ──────────────────────────────────────────────────────────
    private void inicializarMapa() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // usamos nuestro FAB

        // Tap en el mapa → selecciona ubicación
        googleMap.setOnMapClickListener(this::seleccionarUbicacion);

        // Long press → zoom in rápido
        googleMap.setOnMapLongClickListener(latLng ->
                googleMap.animateCamera(CameraUpdateFactory.zoomIn()));

        solicitarPermisoUbicacion();
    }

    // ── Permisos ──────────────────────────────────────────────────────
    private void solicitarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            activarMiUbicacion();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }
    }

    private void activarMiUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            activarMiUbicacion();
        } else {
            mostrarSnackbar("⚠️ Permiso de ubicación denegado. Toca el mapa manualmente.");
        }
    }

    // ── Obtener ubicación actual ──────────────────────────────────────
    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            solicitarPermisoUbicacion();
            return;
        }

        chipUbicacionActual.setText("Buscando...");
        chipUbicacionActual.setEnabled(false);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            chipUbicacionActual.setText("Mi ubicación");
            chipUbicacionActual.setEnabled(true);

            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                seleccionarUbicacion(latLng);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
            } else {
                mostrarSnackbar("No se pudo obtener la ubicación GPS. Toca el mapa.");
            }
        }).addOnFailureListener(e -> {
            chipUbicacionActual.setText("Mi ubicación");
            chipUbicacionActual.setEnabled(true);
            mostrarSnackbar("Error GPS: " + e.getMessage());
        });
    }

    // ── Seleccionar punto en el mapa ──────────────────────────────────
    private void seleccionarUbicacion(LatLng latLng) {
        googleMap.clear();

        // Marcador verde
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Zona Agrícola")
                .snippet(String.format("%.5f, %.5f", latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Círculo de área aproximada
        googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(50)
                .strokeColor(0xFF39FF14)
                .fillColor(0x2239FF14)
                .strokeWidth(2f));

        latitudSeleccionada = latLng.latitude;
        longitudSeleccionada = latLng.longitude;

        // Actualizar campos
        etLatitud.setText(String.format(Locale.US, "%.6f", latLng.latitude));
        etLongitud.setText(String.format(Locale.US, "%.6f", latLng.longitude));

        // Animación del status
        animarStatusCoords(true);
        tvCoordsStatus.setText("✓ Ubicación fijada");

        // Geocodificación inversa en background
        buscarDireccion(latLng);
    }

    // ── Geocodificación inversa: coordenadas → nombre de lugar ────────
    private void buscarDireccion(LatLng latLng) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, new Locale("es", "PE"));
                List<Address> addresses = geocoder.getFromLocation(
                        latLng.latitude, latLng.longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address addr = addresses.get(0);

                    // Construir nombre sugerido
                    String localidad = addr.getLocality() != null
                            ? addr.getLocality()
                            : (addr.getSubAdminArea() != null ? addr.getSubAdminArea() : "");

                    // Si el campo nombre está vacío, sugerirlo
                    runOnUiThread(() -> {
                        String nombreActual = etNombreZona.getText() != null
                                ? etNombreZona.getText().toString().trim() : "";
                        if (nombreActual.isEmpty() && !localidad.isEmpty()) {
                            etNombreZona.setText("Zona " + localidad);
                            etNombreZona.setSelection(etNombreZona.getText().length());
                        }

                        // Actualizar status con la dirección encontrada
                        String distrito = addr.getSubLocality() != null
                                ? addr.getSubLocality() : localidad;
                        if (!distrito.isEmpty()) {
                            tvCoordsStatus.setText("✓ " + distrito);
                        }
                    });
                }
            } catch (Exception ignored) {
                // Geocoder no disponible, no pasa nada
            }
        }).start();
    }

    // ── Validación y guardado ─────────────────────────────────────────
    private void guardarZona() {
        String nombre = etNombreZona.getText() != null
                ? etNombreZona.getText().toString().trim() : "";
        String descripcion = etDescripcion.getText() != null
                ? etDescripcion.getText().toString().trim() : "";

        boolean hayError = false;

        if (nombre.isEmpty()) {
            tilNombreZona.setError("Ingresa el nombre de la zona");
            etNombreZona.requestFocus();
            hayError = true;
        }
        if (descripcion.isEmpty()) {
            tilDescripcion.setError("Ingresa una descripción");
            if (!hayError) etDescripcion.requestFocus();
            hayError = true;
        }
        if (latitudSeleccionada == null || longitudSeleccionada == null) {
            mostrarSnackbar("📍 Toca el mapa para fijar la ubicación del terreno");
            // Efecto de pulso en el mapa para llamar la atención
            pulsarCardCoords();
            hayError = true;
        }

        if (hayError) return;

        // Todo OK → enviar
        String fecha = java.time.OffsetDateTime.now().toString();

        ZonaAgricola zona = new ZonaAgricola();
        zona.setNombreZona(nombre);
        zona.setDescripcion(descripcion);
        zona.setLatitud(latitudSeleccionada);
        zona.setLongitud(longitudSeleccionada);
        zona.setFechaRegistro(fecha);

        setEstadoBoton(true);

        apiService.crearZona(zona).enqueue(new Callback<ZonaAgricola>() {
            @Override
            public void onResponse(Call<ZonaAgricola> call, Response<ZonaAgricola> response) {
                setEstadoBoton(false);
                if (response.isSuccessful() && response.body() != null) {
                    mostrarExito(response.body());
                }else {
                    try {
                        String err = response.errorBody() != null
                                ? response.errorBody().string() : "Error desconocido";
                        mostrarSnackbar("Error " + response.code() + ": " + err);
                    } catch (Exception e) {
                        mostrarSnackbar("Error del servidor: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ZonaAgricola> call, Throwable t) {
                setEstadoBoton(false);
                mostrarSnackbar("Sin conexión: " + t.getMessage());
            }
        });
    }

    // ── Mostrar éxito y cerrar ────────────────────────────────────────
    private void mostrarExito(ZonaAgricola zonaCreada) {

        btnGuardar.setIconResource(android.R.drawable.checkbox_on_background);
        btnGuardar.setText("¡Zona registrada! 🌱");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent resultIntent = new Intent();
            resultIntent.putExtra("zona_id", zonaCreada.getZonaID());
            resultIntent.putExtra("zona_nombre", zonaCreada.getNombreZona());
            resultIntent.putExtra("zona_lat", zonaCreada.getLatitud());
            resultIntent.putExtra("zona_lng", zonaCreada.getLongitud());

            setResult(RESULT_OK, resultIntent);
            finish();

        }, 1200);
    }


    // ── Helpers de UI ─────────────────────────────────────────────────

    private void setEstadoBoton(boolean cargando) {
        btnGuardar.setEnabled(!cargando);
        btnGuardar.setText(cargando ? "Guardando..." : "Registrar Zona Agrícola");
        if (cargando) btnGuardar.setIconResource(android.R.drawable.ic_popup_sync);
        else btnGuardar.setIconResource(android.R.drawable.ic_menu_save);
    }

    // Anima el card de coordenadas con un parpadeo rojo para alertar
    private void pulsarCardCoords() {
        if (cardCoordsStatus == null) return;
        ValueAnimator animator = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                0xFFE8F5E9,   // verde claro normal
                0xFFFFCDD2,   // rojo claro
                0xFFE8F5E9);
        animator.setDuration(600);
        animator.setRepeatCount(2);
        animator.addUpdateListener(anim ->
                cardCoordsStatus.setCardBackgroundColor((int) anim.getAnimatedValue()));
        animator.start();
    }

    // Anima el FAB con efecto rebote
    private void animarFab() {
        fabLocation.animate()
                .scaleX(0.8f).scaleY(0.8f)
                .setDuration(120)
                .withEndAction(() ->
                        fabLocation.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator())
                                .start())
                .start();
    }

    // Anima el badge de estado de coordenadas
    private void animarStatusCoords(boolean exito) {
        if (cardCoordsStatus == null) return;
        int colorDestino = exito ? 0xFFE8F5E9 : 0xFFFFCDD2;
        cardCoordsStatus.setCardBackgroundColor(colorDestino);

        cardCoordsStatus.animate()
                .scaleX(1.1f).scaleY(1.1f)
                .setDuration(150)
                .withEndAction(() ->
                        cardCoordsStatus.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(150)
                                .setInterpolator(new OvershootInterpolator())
                                .start())
                .start();
    }

    // Snackbar en lugar de Toast para mensajes importantes
    private void mostrarSnackbar(String mensaje) {
        Snackbar.make(mainView, mensaje, Snackbar.LENGTH_LONG)
                .setBackgroundTint(0xFF0C1A0E)
                .setTextColor(0xFF39FF14)
                .show();
    }
}