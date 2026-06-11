package com.example.vidaenequilibrio.ui.estadisticas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasFragment extends Fragment {

    private TextView tvTotal, tvConsumidos, tvDesechados;
    private RecyclerView recyclerLogros;
    private ProgressBar progressBar;

    private ApiService api;

    public EstadisticasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        tvTotal = view.findViewById(R.id.tvTotalProductos);
        tvConsumidos = view.findViewById(R.id.tvConsumidos);
        tvDesechados = view.findViewById(R.id.tvDesechados);
        recyclerLogros = view.findViewById(R.id.recyclerLogros);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerLogros.setLayoutManager(new LinearLayoutManager(getContext()));

        api = ApiClient.getApi();

        cargarMetricas();
        cargarLogros();

        return view;
    }

    private void cargarMetricas() {
        progressBar.setVisibility(View.VISIBLE);
        api.getMetricas(1).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body().getAsJsonObject("data");
                    tvTotal.setText("Total productos: " + json.get("total").getAsInt());
                    tvConsumidos.setText("Consumidos: " + json.get("consumidos").getAsInt());
                    tvDesechados.setText("Desechados: " + json.get("desechados").getAsInt());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarLogros() {
        api.getLogros(1).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<JsonObject> logros = response.body();
                    Toast.makeText(getContext(), "Logros obtenidos: " + logros.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
