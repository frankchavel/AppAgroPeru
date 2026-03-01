package Model;

public class ProductoAgricola {
    private int productoAgricolaID;
    private String nombreProducto;
    private String categoria;
    private String unidadMedida;

    public ProductoAgricola() {
    }

    public ProductoAgricola(String nombreProducto, String categoria, String unidadMedida) {
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
    }

    public int getProductoAgricolaID() {
        return productoAgricolaID;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }
}
