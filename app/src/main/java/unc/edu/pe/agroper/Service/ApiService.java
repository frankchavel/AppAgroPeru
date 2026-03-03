package unc.edu.pe.agroper.Service;

import java.util.List;

import Model.Cultivo;
import Model.Recordatorio;
import Model.Recurso;
import Model.Usuario;
import Model.ZonaAgricola;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/insumo")
    Call<List<Recurso>> getInsumos();

    @POST("api/insumo")
    Call<Recurso> crearInsumo(@Body Recurso recurso);

    @DELETE("api/insumo/{id}")
    Call<Void> eliminarInsumo(@Path("id") int id);
    // GET ALL USUARIOS
    @GET("api/usuario")
    Call<List<Usuario>> getUsuarios();

    // CREATE USUARIO
    @POST("api/usuario")
    Call<Usuario> createUsuario(@Body Usuario usuario);
    @POST("api/zonaagricola")
    Call<ZonaAgricola> crearZona(@Body ZonaAgricola zona);
    @GET("api/zonaagricola")
    Call<List<ZonaAgricola>> obtenerZonas();

    @GET("api/cultivo")
    Call<List<Cultivo>> obtenerCultivos();

    @POST("api/cultivo")
    Call<Cultivo> crearCultivo(@Body Cultivo cultivo);

    @POST("api/recordatorio")
    Call<Cultivo> crearRecordatorio(@Body Recordatorio recordatorio);


}
