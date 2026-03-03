package unc.edu.pe.agroper.Service.Clima;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("temperature")
    public Temperature temperature;

    @SerializedName("relativeHumidity")
    public int humidity;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("precipitationProbability")
    public int precipitationProbability;

    public static class Temperature {
        @SerializedName("degrees")
        public double degrees;
    }

    public static class Wind {
        @SerializedName("speed")
        public Speed speed;
    }

    public static class Speed {
        @SerializedName("value")
        public double value;
    }
}
