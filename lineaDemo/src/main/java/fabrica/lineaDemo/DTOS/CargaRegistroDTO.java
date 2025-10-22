package fabrica.lineaDemo.DTOS;

public class CargaRegistroDTO {
    private String codigoProducto;
    private String codigoComponente;

    private Integer cantidadComponente;
    private Integer puestoActual;

    private UsuarioDTO usuario;


    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public void setPuestoActual(Integer puestoActual) {
        this.puestoActual = puestoActual;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public String getCodigoComponente() {
        return codigoComponente;
    }

    public void setCodigoComponente(String codigoComponente) {
        this.codigoComponente = codigoComponente;
    }

    public Integer getPuestoActual() {
        return puestoActual;
    }

    public Integer getCantidadComponente() {
        return cantidadComponente;
    }

    public void setCantidadComponente(Integer cantidadComponente) {
        this.cantidadComponente = cantidadComponente;
    }
}
