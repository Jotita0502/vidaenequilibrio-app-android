package com.example.vidaenequilibrio.ui.recetas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vidaenequilibrio.R;

public class DetalleRecetaFragment extends Fragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_INGREDIENTES = "ingredientes";
    private static final String ARG_DESCRIPCION = "descripcion";

    public DetalleRecetaFragment() {}

    public static DetalleRecetaFragment newInstance(String nombre, String ingredientes, String descripcion) {
        DetalleRecetaFragment fragment = new DetalleRecetaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_INGREDIENTES, ingredientes);
        args.putString(ARG_DESCRIPCION, descripcion);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detalle_receta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Animación slide-in
        View root = view.findViewById(R.id.detalleRoot);
        root.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom));

        // Referencias
        TextView tvTitulo = view.findViewById(R.id.tvTituloDetalle);
        TextView tvIngredientes = view.findViewById(R.id.tvIngredientesDetalle);
        TextView tvDescripcion = view.findViewById(R.id.tvDescripcionDetalle);
        TextView btnVolver = view.findViewById(R.id.btnVolver);

        // Argumentos
        assert getArguments() != null;
        String nombre = getArguments().getString(ARG_NOMBRE);
        String ingredientes = getArguments().getString(ARG_INGREDIENTES);
        String descripcion = getArguments().getString(ARG_DESCRIPCION);

        // Mostrar texto con títulos bonitos
        tvTitulo.setText(nombre);
        tvIngredientes.setText("🧺 Ingredientes:\n" + ingredientes);
        tvDescripcion.setText("🍳 Preparación:\n" + descripcion);

        // Botón volver
        btnVolver.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }
}
