package com.example.vidaenequilibrio.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.example.vidaenequilibrio.R;
import com.example.vidaenequilibrio.network.ApiClient;
import com.example.vidaenequilibrio.network.ApiService;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            login(email, password);
        });

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void login(String email, String password) {

        ApiService api = ApiClient.getApi();

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        api.login(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "Error del servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                JsonObject json = response.body();
                String status = json.get("status").getAsString();

                if (!status.equals("success")) {
                    // 🔥 Mostrar mensaje de error
                    Toast.makeText(LoginActivity.this, json.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    return;
                }

                JsonObject user = json.getAsJsonObject("user");

                getSharedPreferences("user_prefs", MODE_PRIVATE)
                        .edit()
                        .putInt("id", user.get("id").getAsInt())
                        .putString("nombre", user.get("nombre").getAsString())
                        .putString("email", user.get("email").getAsString())
                        .putString("fecha", user.get("created_at").getAsString())
                        .apply();

                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
