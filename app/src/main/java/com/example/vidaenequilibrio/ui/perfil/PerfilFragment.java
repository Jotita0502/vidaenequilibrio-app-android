package com.example.vidaenequilibrio.ui.perfil;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.ui.LoginActivity;

import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.example.vidaenequilibrio.network.ApiResponse;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView tvNombre, tvCorreo, tvFecha;
    private ImageView imgPerfil;
    private ProgressBar progresoEco;
    private Button btnCerrarSesion, btnEditarPerfil, btnCambiarPass;

    public PerfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        tvNombre = view.findViewById(R.id.tvNombreUsuario);
        tvCorreo = view.findViewById(R.id.tvCorreoUsuario);
        tvFecha = view.findViewById(R.id.tvFechaRegistro);
        imgPerfil = view.findViewById(R.id.imgPerfil);
        progresoEco = view.findViewById(R.id.progresoEco);

        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnCambiarPass = view.findViewById(R.id.btnCambiarPass);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        cargarDatosUsuario();

        btnEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());
        btnCambiarPass.setOnClickListener(v -> mostrarDialogoCambiarPass());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        return view;
    }

    private void cargarDatosUsuario() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);

        tvNombre.setText(prefs.getString("nombre", "Usuario Desconocido"));
        tvCorreo.setText(prefs.getString("email", "correo@ejemplo.com"));
        tvFecha.setText("Registrado el: " + prefs.getString("fecha", "N/A"));

        int userId = prefs.getInt("id", 0);

        cargarNivelEco(userId);
    }

    private void cargarNivelEco(int usuarioId) {

        ApiService api = ApiClient.getApi();

        api.getMetricas(usuarioId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    if (!response.isSuccessful() || response.body() == null) {
                        progresoEco.setProgress(0);
                        return;
                    }

                    JsonObject body = response.body();

                    // VALIDAR QUE DATA EXISTA
                    if (!body.has("data") || body.get("data").isJsonNull()) {
                        progresoEco.setProgress(0);
                        return;
                    }

                    JsonObject data = body.getAsJsonObject("data");

                    // VALIDAR CAMPOS
                    int total = data.has("total") ? data.get("total").getAsInt() : 0;
                    int desechados = data.has("desechados") ? data.get("desechados").getAsInt() : 0;

                    int eco = 0;

                    if (total > 0) {
                        eco = (int) (((double) (total - desechados) / total) * 100);
                    }

                    progresoEco.setProgress(eco);

                } catch (Exception e) {
                    // SI ALGO FALLA, NO CRASHEA
                    progresoEco.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progresoEco.setProgress(0);
            }
        });
    }



    private void cerrarSesion() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);

        prefs.edit().clear().apply();

        Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    private void mostrarDialogoEditarPerfil() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_editar_perfil, null);

        EditText edtNombre = dialogView.findViewById(R.id.edtNombrePerfil);
        EditText edtCorreo = dialogView.findViewById(R.id.edtCorreoPerfil);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarPerfil);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);

        edtNombre.setText(prefs.getString("nombre", ""));
        edtCorreo.setText(prefs.getString("email", ""));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = edtNombre.getText().toString().trim();
            String nuevoCorreo = edtCorreo.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = prefs.getInt("id", 0);

            Map<String, Object> body = new HashMap<>();
            body.put("id", userId);
            body.put("nombre", nuevoNombre);
            body.put("email", nuevoCorreo);

            btnGuardar.setEnabled(false);
            btnGuardar.setText("Guardando...");

            ApiService api = ApiClient.getApi();
            api.updateUser(body).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar cambios");

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse resp = response.body();

                        if ("success".equalsIgnoreCase(resp.getStatus())) {

                            prefs.edit()
                                    .putString("nombre", nuevoNombre)
                                    .putString("email", nuevoCorreo)
                                    .apply();

                            tvNombre.setText(nuevoNombre);
                            tvCorreo.setText(nuevoCorreo);

                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(getContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Respuesta inválida", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar cambios");
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void mostrarDialogoCambiarPass() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_cambiar_pass, null);

        EditText edtActual = dialogView.findViewById(R.id.edtPassActual);
        EditText edtNueva = dialogView.findViewById(R.id.edtPassNueva);
        EditText edtConfirmar = dialogView.findViewById(R.id.edtPassConfirmar);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarPass);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);

        int userId = prefs.getInt("id", 0);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        btnGuardar.setOnClickListener(v -> {
            String actual = edtActual.getText().toString().trim();
            String nueva = edtNueva.getText().toString().trim();
            String confirmar = edtConfirmar.getText().toString().trim();

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nueva.equals(confirmar)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("id", userId);
            body.put("password_actual", actual);
            body.put("password_nueva", nueva);

            btnGuardar.setEnabled(false);
            btnGuardar.setText("Guardando...");

            ApiService api = ApiClient.getApi();
            api.updatePassword(body).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar");

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse resp = response.body();

                        if ("success".equalsIgnoreCase(resp.getStatus())) {
                            Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), resp.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Error del servidor", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar");
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

}
