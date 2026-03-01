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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import unc.edu.pe.agroper.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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
        binding.btnCrearCuenta.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });
        binding.btnIngresar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accederUsuario();
            }
        });
    }

    private void crearUsuario() {
        String usu = binding.etCorreo.getText().toString();
        String pass = binding.etPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, MisCultivosActivity.class);
                            intent.putExtra("v_usu", usu);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void  accederUsuario(){
        String usu = binding.etCorreo.getText().toString();
        String pass = binding.etPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(usu, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, MisCultivosActivity.class);
                            intent.putExtra("v_usu", usu);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}