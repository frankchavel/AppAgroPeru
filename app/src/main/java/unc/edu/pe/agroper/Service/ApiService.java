package unc.edu.pe.agroper.Service;

import java.util.List;
import java.util.Map;

import Model.Cultivo;
import Model.CultivoCompleto;
import Model.CultivoInsumo;
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
    // INSUMOS
    @GET("api/insumo")
    Call<List<Recurso>> getInsumos();

    @POST("api/insumo")
    Call<Recurso> crearInsumo(@Body Recurso recurso);

    @DELETE("api/insumo/{id}")
    Call<Void> eliminarInsumo(@Path("id") int id);


    // USUARIOS
    @GET("api/usuario")
    Call<List<Usuario>> getUsuarios();

    @POST("api/usuario")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    // NUEVO: Obtener usuario por correo
    @GET("api/usuario/usuario/{email}")
    Call<Usuario> obtenerUsuarioPorEmail(@Path("email") String email);


    // ZONAS
    @POST("api/zonaagricola")
    Call<ZonaAgricola> crearZona(@Body ZonaAgricola zona);

    @GET("api/zonaagricola")
    Call<List<ZonaAgricola>> obtenerZonas();


    // CULTIVOS
    @GET("api/cultivo")
    Call<List<Cultivo>> obtenerCultivos();

    @POST("api/cultivo")
    Call<Cultivo> crearCultivo(@Body Cultivo cultivo);

    // 🔥 NUEVOS ENDPOINTS

    @GET("api/cultivo/{id}/completo")
    Call<CultivoCompleto> obtenerCultivoCompleto(@Path("id") int id);

    @GET("api/cultivo/{id}/costo")
    Call<Double> obtenerCostoCultivo(@Path("id") int id);
    @POST("api/cultivoinsumocontroller")
    Call<Void> asignarInsumo(@Body CultivoInsumo cultivoInsumo);
    @POST("api/insumo")
    Call<Void> registrarRecurso(@Body Map<String, Object> body);
    @GET("api/cultivoinsumocontroller")
    Call<List<CultivoInsumo>> obtenerCultivoInsumos();
    @GET("api/cultivo/usuario/{correo}")
    Call<List<Cultivo>> obtenerCultivosPorCorreo(@Path("correo") String correo);

    @GET("api/insumo")
    Call<List<Recurso>> obtenerRecursos();

    @GET("api/cultivo/usuario/correo/{correo}")
    Call<List<Cultivo>> obtenerCultivosPorUsuario(
            @Path(value = "correo", encoded = true) String correo);
    @DELETE("api/cultivo/{id}")
    Call<Void> eliminarCultivo(@Path("id") int id);

    @DELETE("api/zonaagricola/{id}")
    Call<Void> eliminarZona(@Path("id") int id);

}
