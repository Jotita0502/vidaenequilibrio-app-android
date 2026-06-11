package com.example.vidaenequilibrio.network;

import com.example.vidaenequilibrio.models.Producto;
import com.example.vidaenequilibrio.models.Receta;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("register.php")
    Call<JsonObject> register(@Body Map<String, String> body);

    @POST("login.php")
    Call<JsonObject> login(@Body Map<String, String> body);

    @GET("productos.php")
    Call<List<Producto>> listarProductos(@Query("usuario_id") int usuarioId);

    @POST("productos.php")
    Call<JsonObject> productoAction(@Body Map<String, Object> body);

    // ✅ Cambiado: ahora usamos GET porque recetas.php responde a GET
    @POST("recetas.php")
    Call<List<Receta>> getRecetas(@Body Map<String, Object> body);

    @GET("metricas.php")
    Call<JsonObject> getMetricas(@Query("usuario_id") int usuarioId);

    @GET("logros.php")
    Call<List<JsonObject>> getLogros(@Query("usuario_id") int usuarioId);

    // al final de la interfaz ApiService
    @POST("update_user.php")
    Call<ApiResponse> updateUser(@Body Map<String, Object> body);

    @POST("update_password.php")
    Call<ApiResponse> updatePassword(@Body Map<String, Object> body);

}
