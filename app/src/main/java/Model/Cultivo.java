package Model;

public class Cultivo {
    private int cultivoID;
    private int zonaID;
    private int usuarioID;
    private String nombreCultivo;
    private String descripcion;
    private double areaCultivo;
    private String fechaSiembra;
    private String fechaCosechaEstimada;
    private String fechaCosechaReal;
    private String estado;
    private int tiempoRiegoPromedio;
    private String fechaRegistro;

    // Getters y Setters

    public int getCultivoID() { return cultivoID; }
    public void setCultivoID(int cultivoID) { this.cultivoID = cultivoID; }

    public int getZonaID() { return zonaID; }
    public void setZonaID(int zonaID) { this.zonaID = zonaID; }

    public int getUsuarioID() { return usuarioID; }
    public void setUsuarioID(int usuarioID) { this.usuarioID = usuarioID; }

    public String getNombreCultivo() { return nombreCultivo; }
    public void setNombreCultivo(String nombreCultivo) { this.nombreCultivo = nombreCultivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getAreaCultivo() { return areaCultivo; }
    public void setAreaCultivo(double areaCultivo) { this.areaCultivo = areaCultivo; }

    public String getFechaSiembra() { return fechaSiembra; }
    public void setFechaSiembra(String fechaSiembra) { this.fechaSiembra = fechaSiembra; }

    public String getFechaCosechaEstimada() { return fechaCosechaEstimada; }
    public void setFechaCosechaEstimada(String fechaCosechaEstimada) { this.fechaCosechaEstimada = fechaCosechaEstimada; }

    public String getFechaCosechaReal() { return fechaCosechaReal; }
    public void setFechaCosechaReal(String fechaCosechaReal) { this.fechaCosechaReal = fechaCosechaReal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getTiempoRiegoPromedio() { return tiempoRiegoPromedio; }
    public void setTiempoRiegoPromedio(int tiempoRiegoPromedio) { this.tiempoRiegoPromedio = tiempoRiegoPromedio; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
