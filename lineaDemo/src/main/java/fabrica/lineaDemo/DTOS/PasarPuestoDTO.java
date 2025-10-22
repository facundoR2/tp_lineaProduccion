package fabrica.lineaDemo.DTOS;

public class PasarPuestoDTO {
    private String codigoProducto; //el serial generado
    private Integer puestoActual; // el puesto.

    private String codProd; // el codigo del producto en Productos.



    public PasarPuestoDTO(){}

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public Integer getPuestoActual() {
        return puestoActual;
    }

    public void setPuestoActual(Integer puestoActual) {
        this.puestoActual = puestoActual;
    }

    public String getCodProd() {
        return codProd;
    }

    public void setCodProd(String codProd) {
        this.codProd = codProd;
    }
}
