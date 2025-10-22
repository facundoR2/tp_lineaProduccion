package fabrica.lineaDemo.Repositorys;

import fabrica.lineaDemo.Models.FormulaDetalle;
import fabrica.lineaDemo.Models.FormulaPuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormulaPuestosRepository extends JpaRepository<FormulaPuesto,Integer> {

    //buscar resultado por formulaDetalle(el componente) y el orden.
    Optional<FormulaPuesto>findByFormulaDetalleAndOrden(FormulaDetalle formulaDetalle, Integer orden);

   //buscar un solo resultado por puesto Idp
    List<FormulaPuesto> findByPuesto_IdP(Integer idPuesto);

    FormulaPuesto findByFormulaDetalle_Componente_codigoAndPuesto_IdP(String codigoproducto,Integer idPuesto);



    // buscar todos los registro que tengan el mismo nProducto dentro de FormulaDetalle. y este puesto.
    List<FormulaPuesto> findByFormulaDetalle_NproductoAndPuesto_IdP(String codigoProducto,Integer IdPuesto);

    //buscar todos los registros que tengan esta formula.

    List<FormulaPuesto>findByFormulaDetalle_Formula_CodigoFormula(String codigoFormula);




    //buscar todos los registros que tengan esta formula y este puesto.


    List<FormulaPuesto> findByFormulaDetalle_Formula_CodigoFormulaAndPuesto_IdP(String codigoFormula, Integer idPuesto);


    //buscar por codigo de formula y orden.
    Optional<FormulaPuesto> findByFormulaDetalle_Formula_CodigoFormulaAndOrden(String codigoFormula, Integer orden);



    //buscar registros de un componente especifico
    List<FormulaPuesto> findByFormulaDetalleIdFD(Integer idFD);

}
