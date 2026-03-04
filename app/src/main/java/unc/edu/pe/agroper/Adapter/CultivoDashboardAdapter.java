package unc.edu.pe.agroper.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Model.Cultivo;
import unc.edu.pe.agroper.R;

public class CultivoDashboardAdapter extends RecyclerView.Adapter<CultivoDashboardAdapter.ViewHolder> {

    private List<Cultivo> lista;

    public CultivoDashboardAdapter(List<Cultivo> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cultivo_dashboard, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cultivo c = lista.get(position);

        holder.tvNombre.setText(c.getNombreCultivo());
        holder.tvArea.setText(c.getAreaCultivo() + " ha");
        holder.tvEstado.setText(c.getEstado());
        holder.tvFecha.setText("Cosecha: " + c.getFechaCosechaEstimada());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvArea, tvEstado, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
        }
    }
}