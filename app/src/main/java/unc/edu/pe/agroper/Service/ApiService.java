package unc.edu.pe.agroper.Service;

import java.util.List;

import Model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    // GET ALL USUARIOS
    @GET("api/usuario")
    Call<List<Usuario>> getUsuarios();

    // CREATE USUARIO
    @POST("api/usuario")
    Call<Usuario> createUsuario(@Body Usuario usuario);
}
