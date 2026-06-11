package com.example.vidaenequilibrio.ui.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.models.Producto;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private Context context;
    private List<Producto> listaProductos;

    public ProductoAdapter(Context context, List<Producto> listaProductos, OnProductoClickListener listener) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Producto producto = listaProductos.get(position);

        // TEXTOS
        holder.tvNombre.setText(producto.getNombre());
        holder.tvCantidad.setText("Cantidad: " + producto.getCantidad());
        holder.tvFecha.setText("Vence: " + producto.getFecha_caducidad());
        holder.tvCategoria.setText("Categoría: " + producto.getCategoria());

        // ===============================
        //  IMAGEN SEGÚN CATEGORÍA
        // ===============================
        // ===============================
//  IMAGEN SEGÚN CATEGORÍA (IMÁGENES REALES)
// ===============================
        switch (producto.getCategoria()) {

            case "lacteos":
                holder.imgProducto.setImageResource(R.drawable.lacteos);
                break;

            case "frutas":
                holder.imgProducto.setImageResource(R.drawable.frutas);
                break;

            case "granos":
                holder.imgProducto.setImageResource(R.drawable.granos);
                break;

            case "panaderia":
                holder.imgProducto.setImageResource(R.drawable.panaderia);
                break;

            case "carnes":
                holder.imgProducto.setImageResource(R.drawable.carnes);
                break;

            case "pollo":
                holder.imgProducto.setImageResource(R.drawable.pollos);
                break;

            case "verduras":
                holder.imgProducto.setImageResource(R.drawable.verduras);
                break;

            case "bebidas":
                holder.imgProducto.setImageResource(R.drawable.bebidas);
                break;

            case "snacks":
                holder.imgProducto.setImageResource(R.drawable.snacks);
                break;

            case "pescado":
                holder.imgProducto.setImageResource(R.drawable.pescado);
                break;

            case "pastas":
                holder.imgProducto.setImageResource(R.drawable.pastas);
                break;

            default:
                holder.imgProducto.setImageResource(R.drawable.otros);
                break;
        }

        // ===============================
        //  COLORES POR ESTADO
        // ===============================
        if ("desechado".equals(producto.getEstado())) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.tvNombre.setTextColor(Color.parseColor("#C62828"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9"));
            holder.tvNombre.setTextColor(Color.parseColor("#2E7D32"));
        }

        // ===============================
        //  EDITAR
        // ===============================
        holder.itemView.setOnClickListener(v -> mostrarDialogoEditar(producto, position));

        // ===============================
        //  ELIMINAR
        // ===============================
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Seguro que deseas eliminar '" + producto.getNombre() + "'?")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarProducto(producto.getId(), position))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }
    public void updateData(List<Producto> nuevosProductos) {
        this.listaProductos = nuevosProductos;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvFecha, tvCategoria;
        ImageView btnEliminar, imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre     = itemView.findViewById(R.id.tvNombreProducto);
            tvCantidad   = itemView.findViewById(R.id.tvCantidadProducto);
            tvFecha      = itemView.findViewById(R.id.tvFechaCaducidad);
            tvCategoria  = itemView.findViewById(R.id.tvCategoria);
            btnEliminar  = itemView.findViewById(R.id.btnEliminar);
            imgProducto  = itemView.findViewById(R.id.imgProducto);
        }
    }

    // ===========================================
    //  DIALOGO EDITAR
    // ===========================================
    private void mostrarDialogoEditar(Producto producto, int position) {

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_producto, null);

        EditText etNombre  = dialogView.findViewById(R.id.etNombreProducto);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadProducto);
        EditText etFecha   = dialogView.findViewById(R.id.etFechaCaducidad);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoria);

        // Llenar datos actuales
        etNombre.setText(producto.getNombre());
        etCantidad.setText(String.valueOf(producto.getCantidad()));
        etFecha.setText(producto.getFecha_caducidad());

        // Lista categorías
        String[] categorias = {
                "lacteos", "carnes", "pollo", "pescado", "verduras",
                "frutas", "granos", "panaderia", "pastas", "bebidas",
                "snacks", "otros"
        };

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCat);

        // seleccionar categoría actual
        int index = Arrays.asList(categorias).indexOf(producto.getCategoria());
        if (index >= 0) spCategoria.setSelection(index);

        // DatePicker
        etFecha.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dp = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        String f = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", year, (month + 1), dayOfMonth);
                        etFecha.setText(f);
                    }, y, m, d
            );

            dp.show();
        });

        // DIÁLOGO
        new AlertDialog.Builder(context)
                .setTitle("Editar producto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    producto.setNombre(etNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(etCantidad.getText().toString()));
                    producto.setFecha_caducidad(etFecha.getText().toString());
                    producto.setCategoria(spCategoria.getSelectedItem().toString());

                    actualizarProducto(producto, position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ===========================================
    //  ACTUALIZAR PRODUCTO
    // ===========================================
    private void actualizarProducto(Producto producto, int position) {

        ApiService api = ApiClient.getApi();
        Map<String, Object> body = new HashMap<>();

        body.put("accion", "editar");
        body.put("id", producto.getId());
        body.put("nombre", producto.getNombre());
        body.put("cantidad", producto.getCantidad());
        body.put("fecha_caducidad", producto.getFecha_caducidad());
        body.put("categoria", producto.getCategoria());

        api.productoAction(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                JsonObject json = response.body();

                if ("success".equals(json.get("status").getAsString())) {
                    listaProductos.set(position, producto);
                    notifyItemChanged(position);

                    Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ===========================================
    //  ELIMINAR PRODUCTO
    // ===========================================
    private void eliminarProducto(int id, int position) {

        ApiService api = ApiClient.getApi();

        Map<String, Object> body = new HashMap<>();
        body.put("accion", "eliminar");
        body.put("id", id);

        api.productoAction(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                JsonObject json = response.body();

                if ("success".equals(json.get("status").getAsString())) {
                    listaProductos.remove(position);
                    notifyItemRemoved(position);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
        void onEliminarClick(Producto producto);
    }
}
