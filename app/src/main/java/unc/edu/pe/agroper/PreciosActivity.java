package unc.edu.pe.agroper;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.ArrayList;

import unc.edu.pe.agroper.Adapter.PrecioAdapter;
import unc.edu.pe.agroper.Service.Precios.SepaService;

public class PreciosActivity extends BaseActivity {
    private PrecioAdapter adapter;
    private TextView tvUltimaAct;
    private TextView tvMercadoSelector;
    private EditText etBuscar;

    private String categoriaActiva = "Todos";
    private int mercadoActual = SepaService.MERCADO_LIMA_GMML;

    private MaterialButton btnTodos, btnTuberculos, btnHortalizas, btnFrutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_precios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();


        // Configurar menú inferior (desde BaseActivity)
        setupBottomNavigation();
        selectBottomNavigationItem();


        // ── Referencias ───────────────────────────────────────────────
        RecyclerView rv   = findViewById(R.id.rv_precios);
        etBuscar          = findViewById(R.id.et_buscar);
        tvUltimaAct       = findViewById(R.id.tv_ultima_act);
        tvMercadoSelector = findViewById(R.id.tv_mercado_selector);
        btnTodos          = findViewById(R.id.btn_filtro_todos);
        btnTuberculos     = findViewById(R.id.btn_filtro_tuberculos);
        btnHortalizas     = findViewById(R.id.btn_filtro_hortalizas);
        btnFrutas         = findViewById(R.id.btn_filtro_frutas);

        // ── RecyclerView ──────────────────────────────────────────────
        adapter = new PrecioAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // ── Búsqueda en tiempo real ───────────────────────────────────
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrarPorTextoYCategoria(s.toString(), categoriaActiva);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ── Botones de categoría ──────────────────────────────────────
        btnTodos.setOnClickListener(v      -> aplicarFiltro("Todos"));
        btnTuberculos.setOnClickListener(v -> aplicarFiltro("Tubérculo"));
        btnHortalizas.setOnClickListener(v -> aplicarFiltro("Hortaliza"));
        btnFrutas.setOnClickListener(v     -> aplicarFiltro("Fruta"));

        // ── Selector de mercado ───────────────────────────────────────
        tvMercadoSelector.setOnClickListener(v -> mostrarDialogoMercado());

        // ── Carga inicial ─────────────────────────────────────────────
        cargarPrecios();
    }

    private void aplicarFiltro(String categoria) {
        categoriaActiva = categoria;
        actualizarBotonesFiltro();
        adapter.filtrarPorTextoYCategoria(etBuscar.getText().toString(), categoriaActiva);
    }

    // ── Resalta el botón activo ───────────────────────────────────────
    private void actualizarBotonesFiltro() {
        int colorVerde   = android.graphics.Color.parseColor("#2E7D32");
        int colorGris    = android.graphics.Color.parseColor("#E0E0E0");
        int textoBlanco  = android.graphics.Color.WHITE;
        int textoGris    = android.graphics.Color.parseColor("#546E7A");

        MaterialButton[] botones   = {btnTodos, btnTuberculos, btnHortalizas, btnFrutas};
        String[]         categorias = {"Todos", "Tubérculo", "Hortaliza", "Fruta"};

        for (int i = 0; i < botones.length; i++) {
            boolean activo = categorias[i].equals(categoriaActiva);
            botones[i].setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(activo ? colorVerde : android.graphics.Color.TRANSPARENT));
            botones[i].setTextColor(activo ? textoBlanco : textoGris);
            botones[i].setStrokeColor(
                    android.content.res.ColorStateList.valueOf(activo ? colorVerde : colorGris));
        }
    }

    // ── Carga datos del mercado seleccionado ──────────────────────────
    private void cargarPrecios() {
        tvUltimaAct.setText("Cargando precios...");

        SepaService.obtenerPrecios(mercadoActual, new SepaService.Callback() {
            @Override
            public void onSuccess(List<SepaService.PrecioProducto> precios, String fechaActualizacion) {
                precios.addAll(productosExtra(mercadoActual));
                runOnUiThread(() -> {
                    adapter.setLista(precios);
                    adapter.filtrarPorTextoYCategoria("", categoriaActiva);
                    tvUltimaAct.setText("Última actualización: " + fechaActualizacion);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(PreciosActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    tvUltimaAct.setText("Error al cargar datos");
                });
            }
        });
    }

    // ── Diálogo para cambiar mercado ──────────────────────────────────
    private void mostrarDialogoMercado() {
        String[] mercados = {
                "Gran Mercado Mayorista de Lima (GMML)",
                "Mercado Arequipa",
                "Mercado Trujillo",
                "Mercado Cajamarca"
        };
        int[] ids = {
                SepaService.MERCADO_LIMA_GMML,
                SepaService.MERCADO_AREQUIPA,
                SepaService.MERCADO_TRUJILLO,
                SepaService.MERCADO_CAJAMARCA
        };

        new android.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar mercado")
                .setItems(mercados, (dialog, which) -> {
                    mercadoActual = ids[which];
                    tvMercadoSelector.setText(mercados[which]);
                    categoriaActiva = "Todos";
                    actualizarBotonesFiltro();
                    cargarPrecios();
                })
                .show();
    }

    // ── Datos extra por mercado ───────────────────────────────────────
    private List<SepaService.PrecioProducto> productosExtra(int mercado) {
        List<SepaService.PrecioProducto> extra = new ArrayList<>();

        if (mercado == SepaService.MERCADO_LIMA_GMML) {
            extra.add(p("Espinaca",         "Lima",        1.20, 2.50, 1.80, "Hortaliza", "+1.5%", true));
            extra.add(p("Brócoli",          "Lima",        1.50, 3.00, 2.20, "Hortaliza", "+0.8%", true));
            extra.add(p("Pepino",           "Ica",         0.80, 1.80, 1.30, "Hortaliza", "-0.5%", false));
            extra.add(p("Naranja Valencia", "Junín",       0.60, 1.20, 0.90, "Fruta",     "+1.2%", true));
            extra.add(p("Papaya",           "Ucayali",     0.80, 1.50, 1.15, "Fruta",     "-0.8%", false));
            extra.add(p("Uva Red Globe",    "Ica",         2.50, 4.50, 3.50, "Fruta",     "+2.0%", true));
            extra.add(p("Palta Hass",       "La Libertad", 3.00, 6.00, 4.50, "Fruta",     "+3.1%", true));
            extra.add(p("Camote Amarillo",  "Lima",        0.50, 1.00, 0.75, "Tubérculo", "+0.5%", true));
            extra.add(p("Apio",             "Lima",        0.80, 1.60, 1.20, "Hortaliza", "+0.3%", true));
            extra.add(p("Betarraga",        "Lima",        0.60, 1.30, 0.95, "Hortaliza", "0.0%",  true));

        } else if (mercado == SepaService.MERCADO_AREQUIPA) {
            extra.add(p("Cebolla Blanca",   "Arequipa",    0.80, 1.80, 1.30, "Hortaliza", "+1.0%", true));
            extra.add(p("Lechuga",          "Arequipa",    0.60, 1.20, 0.90, "Hortaliza", "0.0%",  true));
            extra.add(p("Durazno",          "Arequipa",    1.50, 3.00, 2.25, "Fruta",     "-1.0%", false));
            extra.add(p("Olluco",           "Puno",        0.80, 1.60, 1.20, "Tubérculo", "+0.3%", true));
            extra.add(p("Habas Verdes",     "Arequipa",    1.20, 2.50, 1.85, "Hortaliza", "+0.7%", true));
            extra.add(p("Pera",             "Arequipa",    1.50, 3.00, 2.25, "Fruta",     "-0.5%", false));

        } else if (mercado == SepaService.MERCADO_TRUJILLO) {
            extra.add(p("Espárrago",        "La Libertad", 3.00, 6.00, 4.50, "Hortaliza", "+2.5%", true));
            extra.add(p("Pimiento",         "La Libertad", 1.50, 3.50, 2.50, "Hortaliza", "+1.8%", true));
            extra.add(p("Maracuyá",         "La Libertad", 1.00, 2.50, 1.75, "Fruta",     "-0.5%", false));
            extra.add(p("Mandarina",        "La Libertad", 0.60, 1.20, 0.90, "Fruta",     "+1.0%", true));
            extra.add(p("Alcachofa",        "La Libertad", 2.00, 4.00, 3.00, "Hortaliza", "+1.2%", true));

        } else if (mercado == SepaService.MERCADO_CAJAMARCA) {
            extra.add(p("Oca",              "Cajamarca",   0.60, 1.20, 0.90, "Tubérculo", "+0.8%", true));
            extra.add(p("Mashua",           "Cajamarca",   0.50, 1.00, 0.75, "Tubérculo", "+0.3%", true));
            extra.add(p("Granadilla",       "Cajamarca",   2.00, 4.00, 3.00, "Fruta",     "+1.5%", true));
            extra.add(p("Col Repollo",      "Cajamarca",   0.40, 0.90, 0.65, "Hortaliza", "-0.3%", false));
            extra.add(p("Fresa",            "Cajamarca",   2.50, 5.00, 3.75, "Fruta",     "+2.0%", true));
        }

        return extra;
    }

    // Helper corto para crear PrecioProducto
    private SepaService.PrecioProducto p(String nombre, String proc,
                                         double min, double max, double prom,
                                         String cat, String variacion, boolean subiendo) {
        SepaService.PrecioProducto pr = new SepaService.PrecioProducto(nombre, proc, min, max, prom);
        pr.categoria = cat;
        pr.variacion = variacion;
        pr.subiendo  = subiendo;
        return pr;
    }
}