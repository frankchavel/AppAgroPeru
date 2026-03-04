package unc.edu.pe.agroper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unc.edu.pe.agroper.R;
import unc.edu.pe.agroper.Service.ApiService;
import unc.edu.pe.agroper.Service.RetrofitClient;

public class ZonaAdapter extends RecyclerView.Adapter<ZonaAdapter.ZonaViewHolder> {

    private List<ZonaAgricola> lista;
    private Context context;
    private ApiService apiService;

    public ZonaAdapter(Context context, List<ZonaAgricola> lista) {
        this.context = context;
        this.lista = lista;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
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

        // 🔴 BOTÓN ELIMINAR
        holder.btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Eliminar zona")
                    .setMessage("¿Seguro que deseas eliminar esta zona agrícola?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        eliminarZona(zona.getZonaID(), position);

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void eliminarZona(int zonaId, int position) {

        apiService.eliminarZona(zonaId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            lista.remove(position);
                            notifyItemRemoved(position);

                            Toast.makeText(context,
                                    "Zona eliminada correctamente",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context,
                                    "Error al eliminar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        Toast.makeText(context,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    static class ZonaViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvDescripcion, tvCoords;
        ImageView btnEliminar;

        public ZonaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreZona);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionZona);
            tvCoords = itemView.findViewById(R.id.tvCoordsZona);

            btnEliminar = itemView.findViewById(R.id.btnEliminarZona);
        }
    }
}