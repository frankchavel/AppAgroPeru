package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.time.OffsetDateTime;

import Model.Usuario; // Asegúrate de que este sea el modelo correcto
import retrofit2.Call;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;
import unc.edu.pe.agroper.databinding.ActivityRegistroUsuarioBinding;

public class RegistroUsuarioActivity extends AppCompatActivity {
    ActivityRegistroUsuarioBinding binding;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegistroUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de Insets (Padding para pantallas con notch/curvas)
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de servicios
        apiService = RetrofitClient.getClient().create(ApiService.class);
        mAuth = FirebaseAuth.getInstance();

        // Listeners
        binding.btnRegistrar.setOnClickListener(v -> crearUsuarioEnFirebase());
        binding.btnBack.setOnClickListener(v -> finish()); // Más eficiente que iniciar MainActivity de nuevo
    }

    private void crearUsuarioEnFirebase() {
        String usu = binding.etCorreo.getText().toString().trim();
        String pass = binding.etConfirmarPassword.getText().toString().trim();
        String name = binding.etNombre.getText().toString().trim();

        if (!validarCampos(name, usu, pass)) return;

        // Bloquear UI para evitar doble clic
        setLoadingState(true);

        mAuth.createUserWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Solo si Firebase tiene éxito, enviamos a nuestra base de datos SQL/API
                        Usuario nuevoUsuario = new Usuario();
                        nuevoUsuario.setNombresCompletos(name);
                        nuevoUsuario.setCorreo(usu);
                        nuevoUsuario.setContrasena(pass);
                        nuevoUsuario.setFechaRegistro(OffsetDateTime.now().toString());

                        enviarUsuarioAApi(nuevoUsuario);
                    } else {
                        setLoadingState(false);
                        snack("Error Firebase: " + task.getException().getMessage());
                    }
                });
    }

    private void enviarUsuarioAApi(Usuario usuario) {
        // Nota: Cambié Call<Cultivo> por Call<Usuario> porque estás registrando un usuario
        apiService.crearUsuario(usuario).enqueue(new retrofit2.Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                setLoadingState(false);
                if (response.isSuccessful()) {
                    Toast.makeText(RegistroUsuarioActivity.this, "Registro completo", Toast.LENGTH_SHORT).show();

                    // Ir a la siguiente actividad
                    Intent intent = new Intent(RegistroUsuarioActivity.this, ParcelaActivity.class);
                    intent.putExtra("v_usu", usuario.getCorreo());
                    startActivity(intent);
                    finish();
                } else {
                    snack("Error en API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                setLoadingState(false);
                snack("Error de conexión API: " + t.getMessage());
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnRegistrar.setEnabled(!isLoading);
        binding.btnRegistrar.setText(isLoading ? "Guardando..." : "Registrarme");
    }

    private boolean validarCampos(String nombre, String email, String password) {
        if (nombre.isEmpty()) {
            binding.etNombre.setError("Ingresa el nombre");
            return false;
        }
        if (email.isEmpty()) {
            binding.etCorreo.setError("Ingrese su correo");
            return false;
        }
        if (password.length() < 6) {
            binding.etConfirmarPassword.setError("Mínimo 6 caracteres");
            return false;
        }
        return true;
    }

    private void snack(String msg) {
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }
}