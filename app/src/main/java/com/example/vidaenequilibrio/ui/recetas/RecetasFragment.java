package com.example.vidaenequilibrio.ui.recetas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.models.Receta;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.example.vidaenequilibrio.ui.adapters.RecetaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetasFragment extends Fragment {

    private EditText edtBuscarReceta;
    private List<Receta> recetasOriginales = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvVacio;
    private RecetaAdapter adapter;
    private List<Receta> listaRecetas = new ArrayList<>();
    private int usuarioId;

    public RecetasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recetas, container, false);

        // Referencias XML
        edtBuscarReceta = view.findViewById(R.id.edtBuscarReceta);
        recyclerView = view.findViewById(R.id.recyclerRecetas);
        progressBar = view.findViewById(R.id.progressRecetas);
        tvVacio = view.findViewById(R.id.tvVacio);

        // Configurar Recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecetaAdapter(getContext(), listaRecetas);
        recyclerView.setAdapter(adapter);

        // Obtener usuario
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        usuarioId = prefs.getInt("id", 0);

        if (usuarioId == 0) {
            tvVacio.setText("Error: usuario no encontrado");
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            cargarRecetas();
        }

        // 🔍 BUSCADOR
        edtBuscarReceta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filtrarRecetas(String texto) {
        List<Receta> filtradas = new ArrayList<>();

        for (Receta r : recetasOriginales) {
            if (r.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                    r.getIngredientes().toLowerCase().contains(texto.toLowerCase()) ||
                    r.getDescripcion().toLowerCase().contains(texto.toLowerCase())) {
                filtradas.add(r);
            }
        }

        adapter.updateData(filtradas);
    }

    private void cargarRecetas() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        ApiService api = ApiClient.getApi();

        Map<String, Object> body = new HashMap<>();
        body.put("usuario_id", usuarioId);

        api.getRecetas(body).enqueue(new Callback<List<Receta>>() {
            @Override
            public void onResponse(Call<List<Receta>> call, Response<List<Receta>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    recetasOriginales = response.body();
                    listaRecetas = new ArrayList<>(recetasOriginales);

                    if (listaRecetas.isEmpty()) {
                        tvVacio.setText("😢 No hay recetas disponibles con tus productos.");
                        tvVacio.setVisibility(View.VISIBLE);
                    } else {
                        tvVacio.setVisibility(View.GONE);
                        adapter.updateData(listaRecetas);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvVacio.setText("Error al obtener las recetas 🍽️");
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Receta>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvVacio.setText("Error de conexión: " + t.getMessage());
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }
}
