package Model;

public class Recurso {
    private int insumoID;
    private String nombreInsumo;
    private String tipoInsumo;
    private String unidadMedida;
    private double precioEstablecido;
    private String fechaRegistro;

    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }
    public void setTipoInsumo(String tipoInsumo) { this.tipoInsumo = tipoInsumo; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public void setPrecioEstablecido(double precioEstablecido) { this.precioEstablecido = precioEstablecido; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
