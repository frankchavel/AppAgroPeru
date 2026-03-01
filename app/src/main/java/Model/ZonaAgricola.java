package Model;

public class ZonaAgricola {
    private int zonaID;
    private String nombreZona;
    private String descripción;
    private float latitud;
    private float longitud;

    public ZonaAgricola() {
    }

    public ZonaAgricola(String nombreZona, String descripción, float latitud, float longitud) {
        this.nombreZona = nombreZona;
        this.descripción = descripción;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getZonaID() {
        return zonaID;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public String getDescripción() {
        return descripción;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }
}
