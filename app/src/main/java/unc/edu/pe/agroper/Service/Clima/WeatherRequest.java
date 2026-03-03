package unc.edu.pe.agroper.Service.Clima;

public class WeatherRequest {
    private Location location;

    public WeatherRequest(double lat, double lng) {
        this.location = new Location(lat, lng);
    }

    public static class Location {
        private double latitude;
        private double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
