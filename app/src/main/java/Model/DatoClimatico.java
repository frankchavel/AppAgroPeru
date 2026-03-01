package Model;

public class DatoClimatico {
    private int datoClimaticoID;
    private int zonaID;
    private String fechaDato;
    private float temperaturaActual;
    private float humedad;
    private float precipitacion;
    private String condicionClimatica;

    public DatoClimatico() {
    }

    public DatoClimatico(int zonaID, String fechaDato, float temperaturaActual, float humedad, float precipitacion, String condicionClimatica) {
        this.zonaID = zonaID;
        this.fechaDato = fechaDato;
        this.temperaturaActual = temperaturaActual;
        this.humedad = humedad;
        this.precipitacion = precipitacion;
        this.condicionClimatica = condicionClimatica;
    }

    public int getDatoClimaticoID() {
        return datoClimaticoID;
    }

    public int getZonaID() {
        return zonaID;
    }

    public String getFechaDato() {
        return fechaDato;
    }

    public float getTemperaturaActual() {
        return temperaturaActual;
    }

    public float getHumedad() {
        return humedad;
    }

    public float getPrecipitacion() {
        return precipitacion;
    }

    public String getCondicionClimatica() {
        return condicionClimatica;
    }
}
