package unc.edu.pe.agroper.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import unc.edu.pe.agroper.R;
import unc.edu.pe.agroper.Service.Precios.SepaService;

public class PrecioAdapter extends RecyclerView.Adapter<PrecioAdapter.ViewHolder> {

    private List<SepaService.PrecioProducto> listaOriginal = new ArrayList<>();
    private List<SepaService.PrecioProducto> listaFiltrada = new ArrayList<>();

    // Emojis por categoría
    private static String emojiPorCategoria(String categoria, int pos) {
        if (categoria == null) categoria = "";
        switch (categoria) {
            case "Tubérculo": return new String[]{"🥔","🍠","🫚"}[pos % 3];
            case "Hortaliza": return new String[]{"🥬","🥦","🥕","🌽","🧅","🫑"}[pos % 6];
            case "Fruta":     return new String[]{"🍋","🍌","🍅","🍊","🍇","🥭","🍑"}[pos % 7];
            default:          return new String[]{"🌾","🫘","🧄"}[pos % 3];
        }
    }

    public void setLista(List<SepaService.PrecioProducto> lista) {
        this.listaOriginal = new ArrayList<>(lista);
        this.listaFiltrada = new ArrayList<>(lista);
        notifyDataSetChanged();
    }

    // Filtro combinado: texto + categoría
    public void filtrarPorTextoYCategoria(String texto, String categoria) {
        listaFiltrada.clear();
        String q = (texto == null) ? "" : texto.toLowerCase().trim();
        boolean todosCat = "Todos".equals(categoria) || categoria == null || categoria.isEmpty();

        for (SepaService.PrecioProducto p : listaOriginal) {
            boolean coincideTexto = q.isEmpty()
                    || p.producto.toLowerCase().contains(q)
                    || p.procedencia.toLowerCase().contains(q);

            boolean coincideCategoria = todosCat
                    || (p.categoria != null && p.categoria.equals(categoria));

            if (coincideTexto && coincideCategoria) {
                listaFiltrada.add(p);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_precio_mercado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SepaService.PrecioProducto producto = listaFiltrada.get(position);

        holder.tvNombre.setText(producto.producto);
        holder.tvProcedencia.setText(producto.procedencia);
        holder.tvPrecio.setText(String.format("S/ %.2f", producto.precioPromedio));

        // Color del precio según tendencia
        holder.tvPrecio.setTextColor(
                producto.subiendo
                        ? Color.parseColor("#2E7D32")
                        : Color.parseColor("#C62828"));

        // Emoji según categoría
        holder.tvIcono.setText(emojiPorCategoria(producto.categoria, position));
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcono, tvNombre, tvProcedencia, tvPrecio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcono       = itemView.findViewById(R.id.tv_icono_producto);
            tvNombre      = itemView.findViewById(R.id.tv_nombre_producto);
            tvProcedencia = itemView.findViewById(R.id.tv_procedencia);
            tvPrecio      = itemView.findViewById(R.id.tv_precio_promedio);
        }
    }
}