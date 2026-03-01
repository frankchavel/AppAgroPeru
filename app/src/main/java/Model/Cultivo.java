package Model;

public class Cultivo {
    private int cultivoID;
    private int zonaID;
    private int usuarioID;
    private int productoAgricolaID;
    private String nombreCultivo;
    private String descripcion;
    private int areaCultivo;
    private String fechaSiembra;
    private String fechaCosechaEstimada;
    private String fechaCosechaReal;
    private String estadoCultivo;
    private int tiempoRiegoPromedio;
    private String fechaRegistro;

    public Cultivo() {
    }

    public Cultivo(int zonaID, int usuarioID, int productoAgricolaID, String nombreCultivo, String descripcion, int areaCultivo, String fechaSiembra, String fechaCosechaEstimada, String fechaCosechaReal, String estadoCultivo, int tiempoRiegoPromedio, String fechaRegistro) {
        this.zonaID = zonaID;
        this.usuarioID = usuarioID;
        this.productoAgricolaID = productoAgricolaID;
        this.nombreCultivo = nombreCultivo;
        this.descripcion = descripcion;
        this.areaCultivo = areaCultivo;
        this.fechaSiembra = fechaSiembra;
        this.fechaCosechaEstimada = fechaCosechaEstimada;
        this.fechaCosechaReal = fechaCosechaReal;
        this.estadoCultivo = estadoCultivo;
        this.tiempoRiegoPromedio = tiempoRiegoPromedio;
        this.fechaRegistro = fechaRegistro;
    }

    public int getCultivoID() { return cultivoID; }

    public int getZonaID() { return zonaID; }

    public void setZonaID(int zonaID) { this.zonaID = zonaID; }

    public int getUsuarioID() { return usuarioID; }

    public int getProductoAgricolaID() { return productoAgricolaID; }

    public void setProductoAgricolaID(int productoAgricolaID) { this.productoAgricolaID = productoAgricolaID; }

    public String getNombreCultivo() { return nombreCultivo; }

    public void setNombreCultivo(String nombreCultivo) { this.nombreCultivo = nombreCultivo; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getAreaCultivo() { return areaCultivo; }

    public void setAreaCultivo(int areaCultivo) { this.areaCultivo = areaCultivo; }

    public String getFechaSiembra() { return fechaSiembra; }

    public void setFechaSiembra(String fechaSiembra) { this.fechaSiembra = fechaSiembra; }

    public String getFechaCosechaEstimada() { return fechaCosechaEstimada; }

    public void setFechaCosechaEstimada(String fechaCosechaEstimada) { this.fechaCosechaEstimada = fechaCosechaEstimada; }

    public String getFechaCosechaReal() { return fechaCosechaReal; }

    public void setFechaCosechaReal(String fechaCosecha) { this.fechaCosechaReal = fechaCosecha; }

    public String getEstadoCultivo() { return estadoCultivo; }

    public void setEstadoCultivo(String estadoCultivo) { this.estadoCultivo = estadoCultivo; }

    public int getTiempoRiegoPromedio() { return tiempoRiegoPromedio; }

    public void setTiempoRiegoPromedio(int tiempoRiegoPromedio) { this.tiempoRiegoPromedio = tiempoRiegoPromedio; }

    public String getFechaRegistro() { return fechaRegistro; }

    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
