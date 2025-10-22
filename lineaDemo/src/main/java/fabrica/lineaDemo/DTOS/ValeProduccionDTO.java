package fabrica.lineaDemo.DTOS;

import java.util.List;

public class ValeProduccionDTO {

    //codigo del producto padre.
    private String codigoProducto;
    //lista de los componentes anexados.
    private List<ComponenteDTO> Componentes;
    // id del valeProduccion Cabecera.
    private Integer idValeProduccion;

    // puesto al que se le agenció el ValeProduccionDetalle
    private Integer puesto;

    //1= completado; 2= en proceso; 3=error/reparacion.
    private Integer Estado;

    //id del usuario que genero el ValeDetalle.
    private Integer UsuarioId;







    //Getters y setters.


    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }


    public void setPuesto(Integer puesto) {
        this.puesto = puesto;
    }

    public Integer getPuesto() {
        return puesto;
    }

    public void setEstado(Integer estado) {
        Estado = estado;
    }

    public Integer getEstado() {
        return Estado;
    }

    public Integer getIdValeProduccion() {
        return idValeProduccion;
    }

    public void setIdValeProduccion(Integer idValeProduccion) {
        this.idValeProduccion = idValeProduccion;
    }

    public Integer getUsuarioId() {
        return UsuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        UsuarioId = usuarioId;
    }

    public void setComponentes(List<ComponenteDTO> componentes) {
        Componentes = componentes;
    }

    public List<ComponenteDTO> getComponentes() {
        return Componentes;
    }
}
