package com.example.vidaenequilibrio.ui.inventario;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.models.Producto;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.example.vidaenequilibrio.ui.adapters.ProductoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private ProductoAdapter adapter;
    private List<Producto> listaProductos = new ArrayList<>();
    private ApiService api;

    private int usuarioId; // Temporal (luego vendrá del login)

    public InventarioFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        recyclerView = view.findViewById(R.id.recyclerInventario);
        progressBar = view.findViewById(R.id.progressBar);
        fabAdd = view.findViewById(R.id.fabAddProducto);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoAdapter(getContext(), listaProductos, null);
        recyclerView.setAdapter(adapter);

        Spinner spFiltro = view.findViewById(R.id.spFiltroCategoria);

        String[] categoriasFiltro = {
                "Todas",
                "lacteos", "carnes", "pollo", "pescado", "verduras",
                "frutas", "granos", "panaderia", "pastas", "bebidas",
                "snacks", "otros"
        };

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoriasFiltro
        );
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltro.setAdapter(adapterFiltro);

        spFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoria = parent.getItemAtPosition(position).toString();
                aplicarFiltro(categoria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        api = ApiClient.getApi();

        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        usuarioId = prefs.getInt("id", 0);

        if (usuarioId == 0) {
            Toast.makeText(getContext(), "Error: usuario no encontrado", Toast.LENGTH_LONG).show();
        }


        fabAdd.setOnClickListener(v -> mostrarDialogAgregar());

        cargarProductos();
        return view;

    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);
        api.listarProductos(usuarioId).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos = response.body();
                    adapter.updateData(listaProductos);
                } else {
                    Toast.makeText(getContext(), "Error al obtener productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void aplicarFiltro(String categoria) {
        if (categoria.equals("Todas")) {
            adapter.updateData(listaProductos);
            return;
        }

        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : listaProductos) {
            if (p.getCategoria() != null && p.getCategoria().equals(categoria)) {
                filtrados.add(p);
            }
        }

        adapter.updateData(filtrados);
    }


    private void mostrarDialogAgregar() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_producto, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadProducto);
        EditText etFecha = dialogView.findViewById(R.id.etFechaCaducidad);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoria);

        // ============================
        //   CATEGORÍAS
        // ============================
        String[] categorias = {
                "lacteos", "carnes", "pollo", "pescado", "verduras",
                "frutas", "granos", "panaderia", "pastas", "bebidas",
                "snacks", "otros"
        };

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCat);

        // ============================
        //   DATE PICKER
        // ============================
        etFecha.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                year1, (month1 + 1), dayOfMonth);
                        etFecha.setText(fechaSeleccionada);
                    },
                    year, month, day
            );
            datePicker.show();
        });

        // ============================
        //   DIÁLOGO
        // ============================
        new AlertDialog.Builder(getContext())
                .setTitle("Agregar producto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String nombre = etNombre.getText().toString().trim();
                    String cantidad = etCantidad.getText().toString().trim();
                    String fecha = etFecha.getText().toString().trim();
                    String categoria = spCategoria.getSelectedItem().toString();

                    if (nombre.isEmpty() || cantidad.isEmpty() || fecha.isEmpty()) {
                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    agregarProducto(nombre, cantidad, fecha, categoria);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void agregarProducto(String nombre, String cantidad, String fecha, String categoria) {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> body = new HashMap<>();
        body.put("accion", "agregar");
        body.put("usuario_id", usuarioId);
        body.put("nombre", nombre);
        body.put("cantidad", Integer.parseInt(cantidad));
        body.put("fecha_caducidad", fecha);
        body.put("categoria", categoria);

        api.productoAction(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body();

                    if (json.get("status").getAsString().equals("success")) {
                        Toast.makeText(getContext(), "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
                        cargarProductos();
                    } else {
                        Toast.makeText(getContext(),
                                "Error: " + json.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
