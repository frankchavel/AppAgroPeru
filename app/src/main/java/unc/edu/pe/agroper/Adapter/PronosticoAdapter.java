package unc.edu.pe.agroper.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import unc.edu.pe.agroper.R;
import unc.edu.pe.agroper.Service.Clima.ForecastResponse;

public class PronosticoAdapter
        extends RecyclerView.Adapter<PronosticoAdapter.ViewHolder> {

    private List<ForecastResponse.ForecastDay> lista;

    public PronosticoAdapter(List<ForecastResponse.ForecastDay> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pronostico_lluvia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ForecastResponse.ForecastDay day = lista.get(position);

        holder.tvDia.setText(day.displayDate.text);
        holder.tvProbabilidad.setText(
                day.precipitationProbability.percent + "%"
        );
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDia, tvProbabilidad;

        ViewHolder(View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tv_dia);
            tvProbabilidad = itemView.findViewById(R.id.tv_probabilidad);
        }
    }
}