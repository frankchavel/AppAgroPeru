package unc.edu.pe.agroper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Configurar el BottomNavigationView (debe llamarse después de setContentView)
     */
    protected void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        if (bottomNavigationView == null) return;

        // Configurar listener para todos los items del menú (Versión moderna)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // No hacer nada si ya estamos en la Activity actual
            if (isCurrentActivity(itemId)) {
                return true;
            }

            // Navegar según el item seleccionado
            navigateToDestination(itemId);
            return true;
        });
    }

    /**
     * Verificar si el item corresponde a la Activity actual
     */
    private boolean isCurrentActivity(int menuItemId) {
        String currentClassName = this.getClass().getSimpleName();

        if (menuItemId == R.id.nav_inicio && currentClassName.equals("ParcelaActivity")) {
            return true;
        } else if (menuItemId == R.id.nav_cultivos && currentClassName.equals("MisCultivosActivity")) {
            return true;
        } else if (menuItemId == R.id.nav_mapa && currentClassName.equals("LocalizacionCultivoActivity")) {
            return true;
        } else if (menuItemId == R.id.nav_calendario && currentClassName.equals("CalendarioActivity")) {
            return true;
        } else if (menuItemId == R.id.nav_mercado && currentClassName.equals("PreciosActivity")) {
            return true;
        }
        return false;
    }

    /**
     * Navegar a la Activity correspondiente
     */
    private void navigateToDestination(int menuItemId) {
        Intent intent = null;

        if (menuItemId == R.id.nav_inicio) {
            intent = new Intent(this, ParcelaActivity.class);
        } else if (menuItemId == R.id.nav_cultivos) {
            intent = new Intent(this, MisCultivosActivity.class);
            // Pasar el email del usuario si está autenticado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                intent.putExtra("v_usu", user.getEmail());
            }
        } else if (menuItemId == R.id.nav_mapa) {
            intent = new Intent(this, LocalizacionCultivoActivity.class);
        } else if (menuItemId == R.id.nav_calendario) {
            intent = new Intent(this, CalendarioActivity.class);
        } else if (menuItemId == R.id.nav_mercado) {
            intent = new Intent(this, PreciosActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Cerrar la Activity actual
        }
    }

    /**
     * Seleccionar el item correcto en el menú según la Activity actual
     */
    protected void selectBottomNavigationItem() {
        if (bottomNavigationView == null) return;

        String currentClassName = this.getClass().getSimpleName();

        if (currentClassName.equals("ParcelaActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_inicio);
        } else if (currentClassName.equals("MisCultivosActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cultivos);
        } else if (currentClassName.equals("LocalizacionCultivoActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_mapa);
        } else if (currentClassName.equals("CalendarioActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_calendario);
        } else if (currentClassName.equals("PreciosActivity")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_mercado);
        }
    }
}