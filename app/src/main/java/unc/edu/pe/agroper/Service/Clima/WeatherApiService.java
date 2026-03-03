package unc.edu.pe.agroper.Service.Clima;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("v1/currentConditions:lookup")
    Call<WeatherResponse> getWeather(
            @Query("location.latitude") double lat,
            @Query("location.longitude") double lng
    );
}
