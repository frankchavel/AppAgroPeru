package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import Model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;
import unc.edu.pe.agroper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnRegistrar.setOnClickListener(v -> crearUsuario());
        binding.btnIngresar.setOnClickListener(v -> accederUsuario());
    }

    private void crearUsuario() {

        String usu = binding.etCorreo.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();

        if (!validarCampos(usu, pass)) return;

        mAuth.createUserWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, MisCultivosActivity.class);
                        intent.putExtra("v_usu", usu);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void accederUsuario() {

        String usu = binding.etCorreo.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();

        if (!validarCampos(usu, pass)) return;

        mAuth.signInWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, ParcelaActivity.class);
                        intent.putExtra("v_usu", usu);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validarCampos(String email, String password) {

        if (email.isEmpty()) {
            binding.etCorreo.setError("Ingrese su correo");
            binding.etCorreo.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Ingrese su contraseña");
            binding.etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Mínimo 6 caracteres");
            binding.etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
