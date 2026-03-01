package Model;

public class Alerta {
    private int alertaID;
    private int zonaID;
    private String tipoAlerta;
    private String nivelSeveridad;
    private String descripcion;
    private String fechaEmision;
    private String fechaInicio;
    private String fechaFin;

    public Alerta() {
    }

    public Alerta(int zonaID, String tipoAlerta, String nivelSeveridad, String descripcion, String fechaEmision, String fechaInicio, String fechaFin) {
        this.zonaID = zonaID;
        this.tipoAlerta = tipoAlerta;
        this.nivelSeveridad = nivelSeveridad;
        this.descripcion = descripcion;
        this.fechaEmision = fechaEmision;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getAlertaID() { return alertaID; }

    public int getZonaID() { return zonaID; }

    public String getTipoAlerta() { return tipoAlerta; }

    public String getNivelSeveridad() { return nivelSeveridad; }

    public String getDescripcion() { return descripcion; }

    public String getFechaEmision() { return fechaEmision; }

    public String getFechaInicio() { return fechaInicio; }

    public String getFechaFin() { return fechaFin; }
}
