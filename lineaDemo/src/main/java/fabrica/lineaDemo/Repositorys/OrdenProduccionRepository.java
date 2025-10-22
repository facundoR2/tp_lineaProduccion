package fabrica.lineaDemo.Repositorys;

import fabrica.lineaDemo.Models.OrdenProduccion;
import fabrica.lineaDemo.Models.ValeProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion,Integer> {


    Optional<OrdenProduccion> findFirstByOrderByIdAsc();

}
