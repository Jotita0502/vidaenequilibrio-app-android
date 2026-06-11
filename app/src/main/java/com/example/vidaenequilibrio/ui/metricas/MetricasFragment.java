package com.example.vidaenequilibrio.ui.metricas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.models.Producto;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MetricasFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private LinearLayout layoutLogros;
    private TextView tvResumen;

    private ApiService api;
    private int usuarioId; // cambiar luego si se usa sesión real

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_metricas, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        layoutLogros = view.findViewById(R.id.layoutLogros);
        tvResumen = view.findViewById(R.id.tvResumen);

        api = ApiClient.getApi();

        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        usuarioId = prefs.getInt("id", 0);

        if (usuarioId == 0) {
            Toast.makeText(getContext(), "Error: usuario no encontrado", Toast.LENGTH_LONG).show();
        }


        cargarMetricas();

        return view;
    }

    private void cargarMetricas() {
        api.listarProductos(usuarioId).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    generarGraficos(response.body());
                    generarLogros(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al obtener los productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generarGraficos(List<Producto> productos) {

        if (productos.isEmpty()) {
            pieChart.setNoDataText("No hay productos registrados");
            barChart.setNoDataText("No hay datos para mostrar");
            tvResumen.setText("Aún no tienes productos guardados.");
            return;
        }

        // ===============================
        // Cálculo de productos vigentes/vencidos
        // ===============================
        int vigentes = 0;
        int vencidos = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date hoy = new Date();

        for (Producto p : productos) {
            try {
                Date fecha = sdf.parse(p.getFecha_caducidad());
                if (fecha != null) {
                    if (fecha.before(hoy)) vencidos++;
                    else vigentes++;
                }
            } catch (ParseException e) {
                vigentes++;
            }
        }

        // ===============================
        // PIE CHART (Eco Style)
        // ===============================
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(vigentes, "Vigentes"));
        entries.add(new PieEntry(vencidos, "Vencidos"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                ContextCompat.getColor(getContext(), R.color.eco_green),
                ContextCompat.getColor(getContext(), R.color.eco_red)
        );
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.eco_text_dark));

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(13f);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.animateY(900, Easing.EaseInOutQuad);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.invalidate();

        // ===============================
        // BAR CHART (Eco Style)
        // ===============================
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < productos.size(); i++) {
            barEntries.add(new BarEntry(i, productos.get(i).getCantidad()));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Cantidad por producto");
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.eco_blue));
        barDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.eco_text_dark));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(900);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(false);

        barChart.invalidate();

        // ===============================
        // TEXTO DE RESUMEN
        // ===============================
        tvResumen.setText(
                "Tienes " + productos.size() +
                        " productos: " + vigentes + " vigentes y " + vencidos + " vencidos."
        );
    }

    private void generarLogros(List<Producto> productos) {

        layoutLogros.removeAllViews();

        int total = productos.size();
        int vencidos = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date hoy = new Date();

        for (Producto p : productos) {
            try {
                Date fecha = sdf.parse(p.getFecha_caducidad());
                if (fecha != null && fecha.before(hoy)) vencidos++;
            } catch (Exception ignored) {
            }
        }

        agregarLogro("🥚 Primer producto agregado", total > 0);
        agregarLogro("🧺 Inventario en crecimiento (5+ productos)", total >= 5);
        agregarLogro("💪 Sin productos vencidos", vencidos == 0);
        agregarLogro("🌱 Mantienes tus productos al día", total > 3 && vencidos == 0);
    }

    private void agregarLogro(String texto, boolean logrado) {
        TextView tv = new TextView(getContext());
        tv.setText((logrado ? "✅ " : "❌ ") + texto);
        tv.setTextSize(16f);
        tv.setPadding(0, 8, 0, 8);
        tv.setTextColor(
                logrado ?
                        ContextCompat.getColor(getContext(), R.color.eco_green) :
                        ContextCompat.getColor(getContext(), R.color.eco_text_light)
        );

        layoutLogros.addView(tv);
    }
}
