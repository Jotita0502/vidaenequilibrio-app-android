package com.example.vidaenequilibrio.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.ui.inventario.InventarioFragment;
import com.example.vidaenequilibrio.ui.recetas.RecetasFragment;
import com.example.vidaenequilibrio.ui.metricas.MetricasFragment;
import com.example.vidaenequilibrio.ui.perfil.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ahora carga el layout principal

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.menu_inventario) selected = new InventarioFragment();
            else if (id == R.id.menu_recetas) selected = new RecetasFragment();
            else if (id == R.id.menu_metricas) selected = new MetricasFragment();
            else if (id == R.id.menu_perfil) selected = new PerfilFragment();

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
                return true;
            }
            return false;
        });

        // Fragment inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InventarioFragment())
                    .commit();
        }
    }
}
