package unc.edu.pe.agroper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Cultivo;
import unc.edu.pe.agroper.R;

public class CultivoAdapter extends RecyclerView.Adapter<CultivoAdapter.ViewHolder> {
    private final Context context;
    private List<Cultivo> lista;

    public CultivoAdapter(Context context, List<Cultivo> lista) {
        this.context = context;
        this.lista = lista != null ? lista : new ArrayList<>();
    }

    public void actualizarLista(List<Cultivo> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mi_cultivo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cultivo cultivo = lista.get(position);

        holder.tvNombre.setText(
                cultivo.getNombreCultivo() != null ?
                        cultivo.getNombreCultivo() : "Sin nombre");

        holder.tvTipo.setText(
                cultivo.getDescripcion() != null ?
                        cultivo.getDescripcion() : "Sin descripción");

        holder.tvEstado.setText(
                cultivo.getEstado() != null ?
                        cultivo.getEstado().toUpperCase() : "ACTIVO");

        holder.tvArea.setText(
                String.format(Locale.getDefault(), "%.2f ha",
                        cultivo.getAreaCultivo()));

        holder.tvZona.setText("Zona ID: " + cultivo.getZonaID());

        // ───────── PROGRESO Y COSECHA REAL ─────────

        try {

            String fechaSiembraStr = cultivo.getFechaSiembra();
            String fechaEstimadaStr = cultivo.getFechaCosechaEstimada();

            if (fechaSiembraStr != null && fechaEstimadaStr != null) {

                // Tomamos solo la parte de la fecha (yyyy-MM-dd)
                String soloFechaSiembra = fechaSiembraStr.substring(0, 10);
                String soloFechaEstimada = fechaEstimadaStr.substring(0, 10);

                LocalDate fechaInicio = LocalDate.parse(soloFechaSiembra);
                LocalDate fechaEstimada = LocalDate.parse(soloFechaEstimada);

                LocalDate hoy = LocalDate.now();

                int totalDias = (int) ChronoUnit.DAYS.between(fechaInicio, fechaEstimada);
                int diasTranscurridos = (int) ChronoUnit.DAYS.between(fechaInicio, hoy);
                int diasRestantes = (int) ChronoUnit.DAYS.between(hoy, fechaEstimada);

                if (diasTranscurridos < 0) diasTranscurridos = 0;
                if (diasTranscurridos > totalDias) diasTranscurridos = totalDias;

                holder.tvProgresoDias.setText(
                        "Día " + diasTranscurridos + " de " + totalDias);

                int progreso = (totalDias == 0) ? 0 :
                        (diasTranscurridos * 100) / totalDias;

                holder.progressBar.setProgress(progreso);

                if (diasRestantes > 0) {
                    holder.tvDiasCosecha.setText(diasRestantes + " días");
                } else if (diasRestantes == 0) {
                    holder.tvDiasCosecha.setText("Hoy 🌾");
                } else {
                    holder.tvDiasCosecha.setText("Cosechado");
                }

            } else {

                holder.tvDiasCosecha.setText("Sin fecha");
                holder.tvProgresoDias.setText("Sin progreso");
                holder.progressBar.setProgress(0);
            }

        } catch (Exception e) {

            holder.tvDiasCosecha.setText("Error fecha");
            holder.tvProgresoDias.setText("Error");
            holder.progressBar.setProgress(0);

            Log.e("FECHA_ERROR", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    // ───────── VIEW HOLDER ─────────
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvTipo, tvEstado,
                tvArea, tvZona,
                tvProgresoDias, tvDiasCosecha;

        LinearProgressIndicator progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre_lote);
            tvTipo = itemView.findViewById(R.id.tv_tipo_cultivo);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvZona = itemView.findViewById(R.id.tv_zona);
            tvProgresoDias = itemView.findViewById(R.id.tv_progreso_dias);
            tvDiasCosecha = itemView.findViewById(R.id.tv_dias_cosecha);
            progressBar = itemView.findViewById(R.id.pb_progreso);
        }
    }
}
