package Model;

public class PrecioMercado {
    private int precioMercadoID;
    private int zonaID;
    private int productoAgricolaID;
    private float precioCompra;
    private float precioVenta;
    private String fechaPrecio;

    public PrecioMercado() {
    }

    public int getPrecioMercadoID() {
        return precioMercadoID;
    }

    public int getZonaID() {
        return zonaID;
    }

    public int getProductoAgricolaID() {
        return productoAgricolaID;
    }

    public float getPrecioCompra() {
        return precioCompra;
    }

    public float getPrecioVenta() {
        return precioVenta;
    }

    public String getFechaPrecio() {
        return fechaPrecio;
    }
}
