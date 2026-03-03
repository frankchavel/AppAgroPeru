package Model;

public class ZonaAgricola {
    private int zonaID;
    private String nombreZona;
    private String descripcion;
    private double latitud;
    private double longitud;
    private String fechaRegistro;

    // 🔥 Constructor vacío obligatorio para Retrofit
    public ZonaAgricola() {
    }

    // Constructor completo opcional
    public ZonaAgricola(String nombreZona, String descripcion,
                        double latitud, double longitud,
                        String fechaRegistro) {

        this.nombreZona = nombreZona;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaRegistro = fechaRegistro;
    }

    // GETTERS Y SETTERS

    public String getNombreZona() { return nombreZona; }
    public void setNombreZona(String nombreZona) { this.nombreZona = nombreZona; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getZonaID() { return zonaID; }
    public void setZonaID(int zonaID) { this.zonaID = zonaID; }
}
