package unc.edu.pe.agroper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Cultivo;
import unc.edu.pe.agroper.ActivityAsignarInsumo;
import unc.edu.pe.agroper.DetalleCultivoActivity;
import unc.edu.pe.agroper.R;

public class CultivoAdapter extends RecyclerView.Adapter<CultivoAdapter.ViewHolder> {

    public interface OnAsignarInsumoListener {
        void onAsignarInsumo(Cultivo cultivo);
    }

    private final Context context;
    private List<Cultivo> lista;
    private OnAsignarInsumoListener listenerInsumo;

    public CultivoAdapter(Context context, List<Cultivo> lista) {
        this.context = context;
        this.lista = lista != null ? lista : new ArrayList<>();
    }

    public void setOnAsignarInsumoListener(OnAsignarInsumoListener listener) {
        this.listenerInsumo = listener;
    }

    public void actualizarLista(List<Cultivo> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
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

        holder.tvEstado.setText(
                cultivo.getEstado() != null ?
                        cultivo.getEstado().toUpperCase() : "ACTIVO");

        holder.tvInfo.setText(
                "Área: " + cultivo.getAreaCultivo() +
                        " ha | Zona ID: " + cultivo.getZonaID());

        // -------- PROGRESO --------
        try {
            String fechaSiembraStr = cultivo.getFechaSiembra();
            String fechaEstimadaStr = cultivo.getFechaCosechaEstimada();

            if (fechaSiembraStr != null && fechaEstimadaStr != null
                    && fechaSiembraStr.length() >= 10
                    && fechaEstimadaStr.length() >= 10) {

                String soloFechaSiembra = fechaSiembraStr.substring(0, 10);
                String soloFechaEstimada = fechaEstimadaStr.substring(0, 10);

                LocalDate fechaInicio = LocalDate.parse(soloFechaSiembra);
                LocalDate fechaEstimada = LocalDate.parse(soloFechaEstimada);
                LocalDate hoy = LocalDate.now();

                int totalDias = (int) ChronoUnit.DAYS.between(fechaInicio, fechaEstimada);
                int diasTranscurridos = (int) ChronoUnit.DAYS.between(fechaInicio, hoy);

                if (diasTranscurridos < 0) diasTranscurridos = 0;
                if (diasTranscurridos > totalDias) diasTranscurridos = totalDias;

                int progreso = (totalDias == 0) ? 0 :
                        (diasTranscurridos * 100) / totalDias;

                holder.tvProgresoDias.setText("Día " + diasTranscurridos + " de " + totalDias);
                holder.progressBar.setProgress(progreso);

            } else {
                holder.tvProgresoDias.setText("Sin progreso");
                holder.progressBar.setProgress(0);
            }

        } catch (Exception e) {
            holder.tvProgresoDias.setText("Error fecha");
            holder.progressBar.setProgress(0);
        }

        // -------- BOTÓN DETALLES --------
        if (holder.btnDetalles != null) {
            holder.btnDetalles.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetalleCultivoActivity.class);
                intent.putExtra("cultivoId", cultivo.getCultivoID());
                context.startActivity(intent);
            });
        }

        // -------- BOTÓN ASIGNAR INSUMO --------
        if (holder.btnAsignarInsumo != null) {
            holder.btnAsignarInsumo.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityAsignarInsumo.class);
                intent.putExtra("cultivoId", cultivo.getCultivoID());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvEstado, tvInfo, tvProgresoDias;
        LinearProgressIndicator progressBar;
        MaterialButton btnDetalles, btnAsignarInsumo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_lote);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvInfo = itemView.findViewById(R.id.tv_info);
            tvProgresoDias = itemView.findViewById(R.id.tv_progreso_dias);
            progressBar = itemView.findViewById(R.id.pb_progreso);
            btnDetalles = itemView.findViewById(R.id.btn_detalles);
            btnAsignarInsumo = itemView.findViewById(R.id.btn_asignar_insumo);
        }
    }
}