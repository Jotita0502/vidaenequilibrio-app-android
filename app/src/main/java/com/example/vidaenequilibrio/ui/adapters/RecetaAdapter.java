package com.example.vidaenequilibrio.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.models.Receta;
import com.example.vidaenequilibrio.ui.recetas.DetalleRecetaFragment;

import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.ViewHolder> {

    private Context context;
    private List<Receta> listaRecetas;

    public RecetaAdapter(Context context, List<Receta> listaRecetas) {
        this.context = context;
        this.listaRecetas = listaRecetas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Receta receta = listaRecetas.get(position);

        holder.tvNombre.setText(receta.getNombre());
        holder.tvIngredientes.setText("🧺 Ingredientes: " + receta.getIngredientes());
        holder.tvDescripcion.setText("🍳 " + receta.getDescripcion());

        // 📖 Abrir detalle de la receta
        holder.cardReceta.setOnClickListener(v -> {
            DetalleRecetaFragment detalle = DetalleRecetaFragment.newInstance(
                    receta.getNombre(),
                    receta.getIngredientes(),
                    receta.getDescripcion()
            );

            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_bottom,   // Fragment entra
                            R.anim.slide_out_bottom,  // Fragment sale
                            R.anim.slide_in_bottom,   // Pop enter
                            R.anim.slide_out_bottom   // Pop exit
                    )
                    .replace(R.id.fragment_container, detalle)
                    .addToBackStack(null)
                    .commit();

        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public void updateData(List<Receta> nuevasRecetas) {
        this.listaRecetas = nuevasRecetas;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvIngredientes, tvDescripcion;
        CardView cardReceta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreReceta);
            tvIngredientes = itemView.findViewById(R.id.tvIngredientesReceta);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionReceta);
            cardReceta = itemView.findViewById(R.id.cardReceta);
        }
    }
}
