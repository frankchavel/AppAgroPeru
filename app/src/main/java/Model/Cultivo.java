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

    public String getNombreCultivo() { return nombreCultivo; }

    public String getDescripcion() { return descripcion; }

    public int getAreaCultivo() { return areaCultivo; }

    public String getFechaSiembra() { return fechaSiembra; }

    public String getFechaCosechaEstimada() { return fechaCosechaEstimada; }

    public String getFechaCosechaReal() { return fechaCosechaReal; }

    public String getEstadoCultivo() { return estadoCultivo; }

    public int getTiempoRiegoPromedio() { return tiempoRiegoPromedio; }

    public String getFechaRegistro() { return fechaRegistro; }
}
