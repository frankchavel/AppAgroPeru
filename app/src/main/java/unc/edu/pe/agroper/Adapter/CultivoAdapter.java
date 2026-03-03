package unc.edu.pe.agroper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.OffsetDateTime;
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

    // Permite actualizar lista dinámicamente
    public void actualizarLista(List<Cultivo> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_cultivos, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cultivo cultivo = lista.get(position);

        // 🔒 Evitar NullPointer
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

        // ───────── PROGRESO REAL ─────────
        int diasActuales = 0;
        int totalDias = 90;

        try {
            if (cultivo.getFechaRegistro() != null) {

                OffsetDateTime fechaRegistro =
                        OffsetDateTime.parse(cultivo.getFechaRegistro());

                OffsetDateTime hoy = OffsetDateTime.now();

                diasActuales = (int) ChronoUnit.DAYS.between(
                        fechaRegistro.toLocalDate(),
                        hoy.toLocalDate());

                if (diasActuales < 0) diasActuales = 0;
                if (diasActuales > totalDias) diasActuales = totalDias;
            }
        } catch (Exception e) {
            diasActuales = 0;
        }

        holder.tvProgresoDias.setText(
                "Día " + diasActuales + " de " + totalDias);

        int progreso = (totalDias == 0) ? 0 :
                (diasActuales * 100) / totalDias;

        holder.progressBar.setProgress(progreso);
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    // ───────── VIEW HOLDER ─────────
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvTipo, tvEstado, tvArea,
                tvZona, tvProgresoDias;

        LinearProgressIndicator progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre_lote);
            tvTipo = itemView.findViewById(R.id.tv_tipo_cultivo);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvZona = itemView.findViewById(R.id.tv_zona);
            tvProgresoDias = itemView.findViewById(R.id.tv_progreso_dias);
            progressBar = itemView.findViewById(R.id.pb_progreso);
        }
    }
}
