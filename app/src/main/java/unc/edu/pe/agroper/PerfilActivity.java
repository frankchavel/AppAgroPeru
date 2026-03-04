package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import unc.edu.pe.agroper.databinding.ActivityPerfilBinding;

public class PerfilActivity extends BaseActivity {

    private ActivityPerfilBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();

        // 2. Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        cargarDatosUsuario();

        // 3. Configurar eventos de botones
        configurarBotones();
    }

    private void cargarDatosUsuario() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String correo = currentUser.getEmail();

            if (correo != null) {
                // Poner el correo real en la vista
                binding.tvPerfilCorreo.setText(correo);

                // Extraer el nombre antes del '@' y ponerle mayúscula inicial
                String nombreBase = correo.split("@")[0];
                String nombreFormateado = nombreBase.substring(0, 1).toUpperCase() + nombreBase.substring(1);

                // Actualizar el nombre grande en pantalla
                binding.tvNombreUsuario.setText(nombreFormateado);
            }

            // Datos que por ahora son simulados (hasta que tengas un endpoint de perfil en tu API)
            binding.tvPerfilCelular.setText("+51 Pendiente de registro");
            binding.tvMiembroDesde.setText("Miembro activo");
            binding.tvPerfilUbicacion.setText("Por definir en mapa");
        } else {
            // Si por algún motivo entra sin estar logueado, lo mandamos al login
            cerrarSesion();
        }
    }

    private void configurarBotones() {

        // Botón Cerrar Sesión
        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // Botón Editar Perfil (Simulado)
        binding.btnEditarPerfilOpcion.setOnClickListener(v -> {
            Toast.makeText(this, "Módulo de edición en construcción", Toast.LENGTH_SHORT).show();
            // TODO: Crear EditPerfilActivity
            // startActivity(new Intent(this, EditPerfilActivity.class));
        });

        // Botón Ubicación (Lleva al mapa general)
        binding.btnUbicacionOpcion.setOnClickListener(v -> {
            startActivity(new Intent(this, VisualizarZonasActivity.class));
        });

        // Botón Editar Foto (Simulado)
        binding.btnEditarFoto.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente: Subir foto desde galería", Toast.LENGTH_SHORT).show();
        });
    }

    private void cerrarSesion() {
        // 1. Cerrar sesión en Firebase
        mAuth.signOut();

        // 2. Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // 3. Navegar a la pantalla de Login y limpiar el historial de pantallas
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}