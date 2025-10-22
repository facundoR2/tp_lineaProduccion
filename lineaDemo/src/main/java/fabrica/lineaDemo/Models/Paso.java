package fabrica.lineaDemo.Models;

import jakarta.persistence.*;

@Entity
public class Paso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paso")
    private Long idPaso;

    private String nombre;

    private String descripcion;

    private Integer orden;



    private Integer duracion;

    public Paso(){}

    public Paso(String nombre, String descripcion, Integer duracion, Integer orden){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.orden = orden;
    }

    //getters y setters


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Long getIdPaso() {
        return idPaso;
    }

    public void setIdPaso(Long idPaso) {
        this.idPaso = idPaso;
    }
}
