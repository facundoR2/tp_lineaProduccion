package fabrica.lineaDemo.DTOS;

public class InfoParaProcesoDTO {
    private String codigoProducto;

    private Integer idpuestoActual;


    public InfoParaProcesoDTO(){}


    //G Y S

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Integer getIdpuestoActual() {
        return idpuestoActual;
    }

    public void setIdpuestoActual(Integer idpuestoActual) {
        this.idpuestoActual = idpuestoActual;
    }
}
