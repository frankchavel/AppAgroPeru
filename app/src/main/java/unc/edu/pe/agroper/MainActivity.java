package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import unc.edu.pe.agroper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // 1. SESIÓN AUTOMÁTICA: Si ya está logueado, lo mandamos directo adentro
        if (mAuth.getCurrentUser() != null) {
            irAPantallaPrincipal();
        }

        // 2. BOTÓN INGRESAR (Usamos la sintaxis limpia de tu compañero)
        binding.btnIngresar.setOnClickListener(v -> accederUsuario());

        // 3. BOTÓN CREAR CUENTA (Nos lleva a nuestra pantalla de registro)
        // NOTA: Asegúrate de que el ID del botón en tu XML sea btnCrearCuenta o btnRegistrar según corresponda
        binding.btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }

    private void accederUsuario() {
        String usu = binding.etCorreo.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();

        // Usamos el método de validación limpio de tu compañero
        if (!validarCampos(usu, pass)) return;

        // Iniciar Sesión en Firebase
        mAuth.signInWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "¡Bienvenido de nuevo!", Toast.LENGTH_SHORT).show();
                        irAPantallaPrincipal();
                    } else {
                        Toast.makeText(this, "Error: Credenciales incorrectas", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Método extraído de tu compañero (con una mejora para el formato de correo)
    private boolean validarCampos(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etCorreo.setError("Ingrese un correo válido");
            binding.etCorreo.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            binding.etPassword.setError("Mínimo 6 caracteres");
            binding.etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void irAPantallaPrincipal() {
        // Tu compañero tenía ParcelaActivity, yo te pongo MisCultivosActivity.
        // Cambia esto a la pantalla que sea tu "Inicio" real.
        Intent intent = new Intent(MainActivity.this, MisCultivosActivity.class);
        startActivity(intent);
        finish(); // Cierra el Login
    }
}