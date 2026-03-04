package unc.edu.pe.agroper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.CultivoInsumo;
import Model.Recurso;
import unc.edu.pe.agroper.R;

public class CultivoInsumoAdapter extends RecyclerView.Adapter<CultivoInsumoAdapter.ViewHolder> {

    private final Context context;
    private List<CultivoInsumo> lista;
    private List<Recurso> recursos;

    public CultivoInsumoAdapter(Context context, List<CultivoInsumo> lista, List<Recurso> recursos) {
        this.context = context;
        this.lista = lista != null ? lista : new ArrayList<>();
        this.recursos = recursos != null ? recursos : new ArrayList<>();
    }

    public void actualizarLista(List<CultivoInsumo> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    private String getNombreInsumo(int insumoID) {
        for (Recurso r : recursos) {
            if (r.getInsumoID() == insumoID) {
                return r.getNombreInsumo();
            }
        }
        return "Insumo #" + insumoID;
    }

    private String formatearFecha(String fechaISO) {
        if (fechaISO == null || fechaISO.isEmpty()) return "Sin fecha";
        try {
            SimpleDateFormat entrada = new SimpleDateFormat(
                    fechaISO.contains("T") ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd",
                    Locale.getDefault()
            );
            SimpleDateFormat salida = new SimpleDateFormat(
                    "dd 'de' MMMM 'de' yyyy", new Locale("es", "PE")
            );
            Date date = entrada.parse(fechaISO);
            return salida.format(date);
        } catch (Exception e) {
            return fechaISO;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_insumo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CultivoInsumo item = lista.get(position);

        holder.tvNombreInsumo.setText(getNombreInsumo(item.getInsumoID()));
        holder.tvCantidad.setText("Cantidad: " + item.getCantidad());
        holder.tvCosto.setText("S/ " + String.format(Locale.getDefault(), "%.2f", item.getCostoTotal()));
        holder.tvFecha.setText(formatearFecha(item.getFechaAplicacion()));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreInsumo, tvCantidad, tvCosto, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreInsumo = itemView.findViewById(R.id.tv_insumo_id);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad);
            tvCosto = itemView.findViewById(R.id.tv_costo);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
        }
    }
}