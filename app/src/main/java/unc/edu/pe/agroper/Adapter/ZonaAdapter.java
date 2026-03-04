package unc.edu.pe.agroper.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import Model.ZonaAgricola;
import unc.edu.pe.agroper.R;

public class ZonaAdapter extends RecyclerView.Adapter<ZonaAdapter.ZonaViewHolder> {

    private List<ZonaAgricola> lista;

    public ZonaAdapter(List<ZonaAgricola> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ZonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_zona, parent, false);
        return new ZonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZonaViewHolder holder, int position) {

        ZonaAgricola zona = lista.get(position);

        holder.tvNombre.setText(zona.getNombreZona());
        holder.tvDescripcion.setText(zona.getDescripcion());

        String coords = String.format(Locale.US,
                "Lat: %.5f | Lng: %.5f",
                zona.getLatitud(),
                zona.getLongitud());

        holder.tvCoords.setText(coords);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ZonaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvDescripcion, tvCoords;

        public ZonaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreZona);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionZona);
            tvCoords = itemView.findViewById(R.id.tvCoordsZona);
        }
    }
}

