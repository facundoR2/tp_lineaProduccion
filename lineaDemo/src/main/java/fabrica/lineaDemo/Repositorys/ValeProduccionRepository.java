package fabrica.lineaDemo.Repositorys;

import fabrica.lineaDemo.Models.ValeProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValeProduccionRepository extends JpaRepository<ValeProduccion,Integer> {

    //encontrar vale por serial y estado:(puesto del usuario).
    ValeProduccion findBySerialAndEstado(String codigoProductoFinal,Integer estado);

    //encontrar vale por serial.
    ValeProduccion findBySerial(String codigoProductoFinal);

    //encontrar el primer valeProduccion con el serial y estado en proceso(2)

    Optional<ValeProduccion> findFirstBySerialAndEstadoOrderByIdValeAsc(String serial,Integer estado);

    //encontrar el primer valeProduccion por id ascendente.

    Optional<ValeProduccion> findFirstByEstadoOrderByIdValeAsc(Integer estado);






    List<ValeProduccion>findByOrdenProduccion_IdOP(Integer IdOP);

    List<ValeProduccion>findByEstado(Integer estado);
}
