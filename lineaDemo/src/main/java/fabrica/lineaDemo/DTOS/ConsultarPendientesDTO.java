package fabrica.lineaDemo.DTOS;

public class ConsultarPendientesDTO {
    private String codigoProducto; //codigo del producto a realizar.
    private UsuarioDTO usuario; //usuario que realiza consulta.
    private Integer idPuestoActual;


    public ConsultarPendientesDTO(){}

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public Integer getIdPuestoActual() {
        return idPuestoActual;
    }
}
