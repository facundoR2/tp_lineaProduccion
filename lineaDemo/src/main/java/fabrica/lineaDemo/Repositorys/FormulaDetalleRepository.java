package fabrica.lineaDemo.Repositorys;

import fabrica.lineaDemo.Models.Formula;
import fabrica.lineaDemo.Models.FormulaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulaDetalleRepository extends JpaRepository<FormulaDetalle,Integer> {


    //busca todos los FormulaDetalle cuya formula tenga el mismo codigo.
    List<FormulaDetalle> findByFormula_codigoFormula(String codigoFormula);

    //buscar que tengan el codigoFormula y el CodigoComponente.

    List<FormulaDetalle> findByFormula_codigoFormulaAndComponente_codigo(String codigoFormula,String codigoProducto);



}
