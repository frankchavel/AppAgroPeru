package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import unc.edu.pe.agroper.Service.ApiService;

public class MisCultivosActivity extends AppCompatActivity {
    private RecyclerView rvMisCultivos;
    private View layoutVacio;
    private TextView tvTotalCultivos;
    private TextView tvProximaCosecha;
    private TextView tvTotalHectareas;

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_cultivos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.fab_nuevo_cultivo).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroCultivoActivity.class);
            startActivity(intent);
        });

    }
}