package fabrica.lineaDemo.Repositorys;

import fabrica.lineaDemo.Models.OrdenProduccionDetalles;
import fabrica.lineaDemo.Models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenProduccionDetallesRepository extends JpaRepository<OrdenProduccionDetalles,Integer> {


    OrdenProduccionDetalles findByProducto(Producto producto);

    // buscar la primera orden de Produccion para un producto especifico.
    OrdenProduccionDetalles findByProducto_Codigo (String codigoProducto);
}
