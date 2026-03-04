package unc.edu.pe.agroper.Service.Clima;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("forecastDays")
    public List<ForecastDay> forecastDays;

    public static class ForecastDay {

        @SerializedName("displayDate")
        public DisplayDate displayDate;

        @SerializedName("precipitationProbability")
        public Precipitation precipitationProbability;
    }

    public static class DisplayDate {
        @SerializedName("text")
        public String text;
    }

    public static class Precipitation {
        @SerializedName("percent")
        public int percent;
    }
}
