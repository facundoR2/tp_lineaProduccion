package fabrica.lineaDemo.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "Formula_puestos")
public class FormulaPuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_FP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFD", referencedColumnName = "idFD")
    private FormulaDetalle formulaDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idP", referencedColumnName = "idP")
    private Puesto puesto;

    //orden del puesto dentro de la formula.
    private Integer orden;

    private Integer cantidadComp;

    public FormulaPuesto(){}

    public FormulaPuesto(FormulaDetalle formulaDetalle, Puesto puesto){
        this.formulaDetalle = formulaDetalle;
        this.puesto = puesto;

    }

    //Getters y setters


    public FormulaDetalle getFormulaDetalle() {
        return formulaDetalle;
    }

    public void setFormulaDetalle(FormulaDetalle formulaDetalle) {
        this.formulaDetalle = formulaDetalle;
    }


    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Integer getId_FP() {
        return id_FP;
    }

    public void setId_FP(Integer id_FP) {
        this.id_FP = id_FP;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getCantidadComp() {
        return cantidadComp;
    }

    public void setCantidadComp(Integer cantidadComp) {
        this.cantidadComp = cantidadComp;
    }
}
