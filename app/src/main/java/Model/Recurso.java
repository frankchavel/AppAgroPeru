package Model;

public class Recurso {
    private int insumoID;
    private String nombreInsumo;
    private String tipoInsumo;
    private String unidadMedida;
    private double precioEstablecido;
    private String fechaRegistro;
    public Recurso(int insumoID, String nombreInsumo, String tipoInsumo,
                   String unidadMedida, double precioEstablecido, String fechaRegistro) {
        this.insumoID = insumoID;
        this.nombreInsumo = nombreInsumo;
        this.tipoInsumo = tipoInsumo;
        this.unidadMedida = unidadMedida;
        this.precioEstablecido = precioEstablecido;
        this.fechaRegistro = fechaRegistro;
    }

    public int getInsumoID() { return insumoID; }
    public String getNombreInsumo() { return nombreInsumo; }
    public String getTipoInsumo() { return tipoInsumo; }
    public String getUnidadMedida() { return unidadMedida; }
    public double getPrecioEstablecido() { return precioEstablecido; }
    public String getFechaRegistro() { return fechaRegistro; }
}
