package fabrica.lineaDemo.Services;

import fabrica.lineaDemo.DTOS.*;
import fabrica.lineaDemo.Models.*;
import fabrica.lineaDemo.Repositorys.*;
import jakarta.transaction.Transactional;
import org.apache.naming.java.javaURLContextFactory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProduccionService {


    private final ValeProduccionDetalleRepository detalleRepository;


    private final ValeProduccionRepository produccionRepository;
    private final ProductoRepository productoRepository;

    private final OrdenProduccionDetallesRepository ordenProduccionDetallesRepository;

    private final OrdenProduccionRepository ordenPRepository;

    private final FormulaPuestosRepository formupuestorepo;

    private final UsuarioRepository usuarioRepository;
    private final FormulaDetalleRepository formulaDetalleRepository;
    private final CodigoProduccionService codigoService;

    public ProduccionService(FormulaDetalleRepository formulaDetalleRepository,OrdenProduccionRepository ordenPRepository ,OrdenProduccionDetallesRepository ordenProduccionDetallesRepository ,FormulaPuestosRepository formupuestorepo, UsuarioRepository usuarioRepository,CodigoProduccionService codigoService, ValeProduccionDetalleRepository detalleRepository, ValeProduccionRepository produccionRepository, ProductoRepository productoRepository) {

        this.detalleRepository = detalleRepository;
        this.produccionRepository = produccionRepository;
        this.productoRepository = productoRepository;
        this.codigoService = codigoService;
        this.usuarioRepository = usuarioRepository;
        this.formupuestorepo = formupuestorepo;
        this.ordenProduccionDetallesRepository = ordenProduccionDetallesRepository;
        this.ordenPRepository = ordenPRepository;
        this.formulaDetalleRepository = formulaDetalleRepository;

    }


    //region:funciones sencillas.

    public List<ComponenteDTO> convertirADTO(List<Producto> componentes){

        List<ComponenteDTO> dtos = new ArrayList<>();
        for (Producto com : componentes){
            ComponenteDTO componenteDTO = new ComponenteDTO(com.getNombre(),com.getCodigo(),com.getCantidad());
            dtos.add(componenteDTO);
        }
        return dtos;
    }

    public ComponenteDTO convertirComponente_ADTO(Producto componente,Integer cantidad){
        //generamos el dto.
        ComponenteDTO dto = new ComponenteDTO();
        dto.setCodigoProducto(componente.getCodigo());
        dto.setNombre(componente.getNombre());
        dto.setCantidad(cantidad);

        return dto;
    }

    //region-End: funciones sencillas.

    public ValeLectura iniciarValeLectura(UsuarioDTO usuario, String codigoProducto){

        //validamos que el usuario sea el primero.
        int puesto = usuario.getId();
        if(puesto != 1){
            throw new IllegalArgumentException("el usuario no es el primero de la Linea");
        }
        //generamos un ValeProduccion.
        ValeProduccion nuevoproducto = new ValeProduccion();

        // buscar la orden de produccion
         Optional<OrdenProduccion> primera = ordenPRepository.findFirstByOrderByIdAsc();
        if(primera.isPresent()){
            OrdenProduccion orden = primera.get();
            nuevoproducto.setOrdenProduccion(orden);
            //agregamos el codigo de la formula.
            nuevoproducto.setFormula(orden.getFormula());

        }else {
            throw new IllegalArgumentException("no se Encontro una Orden de Produccion");
        }
        //generamos el codigo.
        String codigo = codigoService.generarCodigo(codigoProducto);
        //agregamos el estado.
        nuevoproducto.setEstado(2);  //1= completado, 2= en proceso, 3 = error

        //agregamos el codigo al ValeProduccion.
        nuevoproducto.setSerial(codigo); // agregamos el codigo al ValeProduccion(cabecera).
        //la fecha del servidor.
        nuevoproducto.setFechaRegistro(LocalDate.now());

        nuevoproducto.setDetalles(null); // se genera vacio.

        //GUARDAMOS EL VALEPRODUCCION.
        produccionRepository.save(nuevoproducto);


        //creamos un vale
        ValeLectura vale = new ValeLectura();
        vale.setIdTrazado(codigo+"-("+primera.get().getId()+")");
        vale.setCodigoProducto(codigo);
        vale.setUsuario(usuario);
        vale.setPuestoActual(1); // colocamos el puesto donde se inicia.
        vale.setEstado("EN_PROCESO");

        return vale;

    }

    //por cada componente registrado, se carga un valeDetalle.
    public void   registrarComponente(CargaRegistroDTO cargaRegistroDTO){


        //paso 1 :buscamos el valeProduccionDetalle con ese usuario y ese codigo producto final.

        //validaciones para evitar Duplicados.
        if(detalleRepository.existsByUsuario_IdUsuarioAndCodigoProducto(cargaRegistroDTO.getUsuario().getId(),cargaRegistroDTO.getCodigoProducto())){
            //validar duplicado si el componente ya fue cargado.
            System.out.println("lleyo hasta aca: 134");
             Optional<ValeProduccionDetalle> existente = detalleRepository.findByCodigoProductoAndComponente_CodigoAndPuesto(cargaRegistroDTO.getCodigoProducto(), cargaRegistroDTO.getCodigoComponente(),cargaRegistroDTO.getUsuario().getId());

            if (existente.isPresent()){
                ValeProduccionDetalle valeRegistrado = existente.get();
                //revisamos si la cantidad es 1 en formulaDetalles.

                FormulaPuesto formulacantidad = formupuestorepo.findByFormulaDetalle_Componente_codigoAndPuesto_IdP(cargaRegistroDTO.getCodigoComponente(),cargaRegistroDTO.getPuestoActual());

                if(formulacantidad.getCantidadComp()== 1){
                    throw new IllegalArgumentException("El componente ya fue registrado para este producto");
                }
                if (formulacantidad.getCantidadComp() > 1){
                    System.out.println("el componente lleva mas de 1: aumentando cantidad");
                    Integer nuevaCantidad = valeRegistrado.getCantidad() +1;
                    System.out.println("nueva Cantidad guardada:"+nuevaCantidad);
                    valeRegistrado.setCantidad(nuevaCantidad);
                    detalleRepository.save(valeRegistrado);
                    return;

                }

            }

        }
        //fin-Validacion.

        //si no existe, registrar el nuevo valeProduccion.
        ValeProduccion valeProduccion = new ValeProduccion();
        //buscamos el valeDetalle con el serial del producto y estado en proceso.
         Optional<ValeProduccion> valeEncontrado =  produccionRepository.findFirstBySerialAndEstadoOrderByIdValeAsc(cargaRegistroDTO.getCodigoProducto(), 2);
        if (valeEncontrado.isPresent()){
            valeProduccion = valeEncontrado.get();
        }else {
            throw new IllegalArgumentException("No se encontro el ValeProduccion con el serial del Producto en estado:Proceso");
        }


        ValeProduccionDetalle nuevodetalle = new ValeProduccionDetalle();
        //guardamos el ValeProduccion.
        produccionRepository.save(valeProduccion);
        nuevodetalle.setValeProduccion(valeProduccion);
        //ingresamos el codigo del producto( final)
        System.out.println("codigoProducto."+cargaRegistroDTO.getCodigoProducto());
        nuevodetalle.setCodigoProducto(cargaRegistroDTO.getCodigoProducto());
        //SE AGREGA CANTIDAD DEL COMPONENTE REGISTRADO.
        nuevodetalle.setCantidad(cargaRegistroDTO.getCantidadComponente());
        //ingresamos la fecha y hora.
        nuevodetalle.setFechaHora(LocalDateTime.now());

        //buscamos el componente por el codigo.
        nuevodetalle.setComponente(productoRepository.findByCodigo(cargaRegistroDTO.getCodigoComponente()).orElseThrow(()-> new IllegalArgumentException("no se encontro el componente con el codigo:"+ cargaRegistroDTO.getCodigoComponente())));
        //buscamos el usuario por el id del usuarioDTO.
        nuevodetalle.setUsuario(usuarioRepository.findById(cargaRegistroDTO.getUsuario().getId()).orElseThrow(()->new IllegalArgumentException("No se encontro un usuario con el id:"+ cargaRegistroDTO.getUsuario().getId())));
        nuevodetalle.setPuesto(cargaRegistroDTO.getPuestoActual());

        nuevodetalle.setEstado(2); // 1= completado; 2 = en_proceso; 5= error;
        detalleRepository.save(nuevodetalle);//guardamos el detalle.
    }

    //cuando pasa de puesto, se consolida los componentes anexados al componente padre.
    public void consolidar(String codigoProducto){
        //cambiamos el estado de los valeDetalle del producto a completado.
        List<ValeProduccionDetalle> detalles = detalleRepository.findByCodigoProducto(codigoProducto);
        for (ValeProduccionDetalle detalle : detalles){
            detalle.setEstado(1); //indicamos que esta completo.
        }
        detalleRepository.saveAll(detalles); //guardamos la modificacion.

        //
        ValeProduccion vale = produccionRepository.findBySerial(codigoProducto);
        //cambiamos el estado a completado.
        vale.setEstado(1);
        produccionRepository.save(vale);
        List<Producto> componentesARestar = new ArrayList<>();
        //paso 2 : restar la cantidad usada a la tabla productos.
        for(ValeProduccionDetalle vpd: detalles){
            Producto comp = new Producto();
            //encontramos el componente por el codigo del valeDetalle.
            comp = productoRepository.findByCodigo(vpd.getComponente().getCodigo()).orElseThrow(() -> new IllegalArgumentException("no se encontro el Producto a modificar en consolidar()"));
            System.out.println("cantidad del producto:"+comp.getCodigo()+": "+ comp.getCantidad());
            System.out.println("cantidad a restar: "+vpd.getCantidad());
            comp.setCantidad(comp.getCantidad() - vpd.getCantidad());
            System.out.println("nueva cantidad:"+comp.getCantidad());
            componentesARestar.add(comp);

        }
        productoRepository.saveAll(componentesARestar);
        //sumamos 1 a la cantidad de producto en Producto.
        Producto prod = productoRepository.findByCodigo(vale.getOrdenProduccion().getProducto().getCodigo()).orElseThrow(() -> new IllegalArgumentException("no se encontro el producto general a aumentar en consolidar()"));
        prod.setCantidad(prod.getCantidad()+1);
        productoRepository.save(prod);
        //aumentamos la cantidad producida en la ordenProduccionDetalles.
        OrdenProduccionDetalles ordenDetalles = ordenProduccionDetallesRepository.findByProducto(vale.getOrdenProduccion().getProducto());
        //validacion por si es null
        if(ordenDetalles.getCantProducida() == null){
            ordenDetalles.setCantProducida(0);
        }
        ordenDetalles.setCantProducida(ordenDetalles.getCantProducida()+1);
        System.out.println("ORDENPRODUCCION: NuevaCantidadProducida = "+ ordenDetalles.getCantProducida());
        ordenProduccionDetallesRepository.save(ordenDetalles);
        System.out.println("ciclo Completado");

    }


    public boolean verificarExistencia(String codigoProducto){
        return detalleRepository.existsByComponente_Codigo(codigoProducto);
    }



    //proceso para cerrar el ciclo del producto, sumando 1 a valeProduccion y restando lo consumido.
    @Transactional //agregando este estereotipo, si algo falla, hace rollBack.
    public void cerrarProducto(Integer idValeProduccion) {

        //paso 1 : obtener el vale Produccion en proceso.
        ValeProduccion valeProduccion = produccionRepository.findById(idValeProduccion).orElseThrow(() -> new IllegalStateException("No existe el vale de produccion"));

        if (valeProduccion.getEstado() !=2){ //2 = en proceso.
            throw new IllegalStateException("El vale no esta en proceso");
        }

        //paso 2: traer todos los valeDetalle asociados.
        List<ValeProduccionDetalle> detalles = detalleRepository.findByValeProduccion(valeProduccion);
        if (detalles.isEmpty()){
            throw new IllegalStateException("No hay componentes registrados para este vale");
        }

        //paso 3: descontar los componentes del inventario.
        for(ValeProduccionDetalle detalle : detalles){
            Producto componente = detalle.getComponente();

            Producto inventario = productoRepository.findByCodigo(componente.getCodigo()).orElseThrow(() -> new IllegalStateException("No se encontro el inventario para el componente: "+componente.getCodigo()));

            //retamos la cantidad usada.

            Integer cantidadUsada = componente.getCantidad();
            inventario.setCantidad(inventario.getCantidad() - cantidadUsada);
            productoRepository.save(inventario);
        }

        //paso 4 : marcar el ValeProduccion como completado.
        valeProduccion.setEstado(1); //1= completado.
        produccionRepository.save(valeProduccion);

        //paso 5: actualizar la cantidad producida en la Orden.

        OrdenProduccionDetalles detalleOP = ordenProduccionDetallesRepository.findByProducto(valeProduccion.getOrdenProduccion().getProducto());

        detalleOP.setCantProducida(detalleOP.getCantProducida() + 1);
        ordenProduccionDetallesRepository.save(detalleOP);

        // paso 6: verificar si aun hay productos pendientes.
        if(detalleOP.getCantProducida() < detalleOP.getCantPrevista()){
            //creamos un nuevo ValeProduccion.
            ValeProduccion nuevoVale = new ValeProduccion();

            //buscamos la OrdenProduccion.


            nuevoVale.setOrdenProduccion(detalles.get(0).getValeProduccion().getOrdenProduccion());
            nuevoVale.setEstado(2);
            nuevoVale.setFormula(detalles.get(0).getValeProduccion().getFormula());
            nuevoVale.setFechaRegistro(LocalDate.now());

        }else {
            OrdenProduccion orden = detalles.get(0).getValeProduccion().getOrdenProduccion();

            orden.setEstado("COMPLETADA");
            ordenPRepository.save(orden);
        }


    }

    public boolean noEsUltimoPuesto(String codigoProductoSerial, Integer puestoActual){

        //buscamos el codigoformula buscando el vale.
        ValeProduccion valel = produccionRepository.findFirstBySerialAndEstadoOrderByIdValeAsc(codigoProductoSerial,2).orElseThrow(() -> new IllegalArgumentException("no se encontro el vale para el serial dado:"));

        //paso 1 : obtenemos todos los formulaPuesto de la formula.
        List<FormulaPuesto> puestosFormula = formupuestorepo.findByFormulaDetalle_Formula_CodigoFormula(valel.getFormula().getCodigoFormula());

        if (puestosFormula.isEmpty()){
            throw new RuntimeException("No hay puestos definidos para la formula "+ codigoProductoSerial);
        }

        //paso 2 : determinar el maximo idP(el ultimoPuesto)
        Integer maxOrden = puestosFormula.stream()
                .map(FormulaPuesto::getPuesto).map(Puesto::getIdP)
                .max(Integer::compareTo)
                .orElseThrow();
        System.out.println("maxOrden: "+maxOrden);
        return puestoActual < maxOrden; // en la formula, seria el orden 3  el maximo.
    }

    public Boolean todosComponentesRegistrados(List<ValeProduccionDetalle> detallesRegistrados, Integer puestoActual){

        //paso 1 : traer todos los los componentes necesarios
        if(detallesRegistrados == null || detallesRegistrados.isEmpty()){
            return false;
        }

        //de los detalles, traemos el valeProduccion que nos indicara la formula.

        ValeProduccion valeProduccion = detallesRegistrados.getFirst().getValeProduccion();

        //paso 2 :obtenemos todos los FormulaPuesto para la formula (codigoformula) y el puesto actual.

        List<FormulaPuesto> formulaPuestos = formupuestorepo.findByFormulaDetalle_Formula_CodigoFormulaAndPuesto_IdP(
                valeProduccion.getFormula().getCodigoFormula(),
                puestoActual);

        //paso 3: extraemos todos los codigos de los componentes necesarios.
        List<String>  componentesNecesarios = formulaPuestos.stream()
                .map(fp -> fp.getFormulaDetalle().getComponente().getNombre()).toList();



        //paso 4:extraemos los componentes efectivamente registrados.
        List<String> componentesRegistrados = detallesRegistrados.stream()
                .map(det -> det.getComponente().getNombre()).toList();

        //verificamos si todos los componentes necesarios estan dentro de los registrados.
        return componentesNecesarios.stream()
                .allMatch(componentesRegistrados::contains);

    }




    //proceso para cargar el proximo puesto con el producto y sus componentes ya anexados.
    public boolean pasarSiguientePuesto (String codigoProductofinal, Integer puestoActual){

        //paso 1: validar Componentes:


        //paso a:
        //obtenemos todos los ValeProduccionDetalle generados por ese puesto para ese  producto.

        List<ValeProduccionDetalle> detallesRegistrados = detalleRepository.findByCodigoProductoAndPuesto(codigoProductofinal,puestoActual);

        //llamamos a la funcion que consulta la formulaDetalle para ver que todos los componentes esten anexados dentro del producto.
        if(!todosComponentesRegistrados(detallesRegistrados, puestoActual)){
            throw new IllegalArgumentException("Faltan Componentes por registrar para el puesto:"+puestoActual);
        }else {
            System.out.println("todos los componentes del puesto estan Registrados en este Vale");
        }
        return true;
    }

    public boolean verificarUltimoComponentePrevioAgregado(Integer idPuestoActual) {
        //

        Integer idpuestoAnterior = idPuestoActual - 1;
        if (idpuestoAnterior == 0){
            // No hay puesto anterior (es el primero o es un numero negativo)
            throw new IllegalArgumentException("no existe un puesto 0 ");

        }
        // 1️⃣ Buscar el puesto anterior
       List<FormulaPuesto> puestoAnterior = formupuestorepo.findByPuesto_IdP(idpuestoAnterior);
        if (puestoAnterior == null) {
            // No hay puesto anterior (es el primero o es un numero negativo)
            throw new IllegalArgumentException("no se encontro un puesto anterior en FormulaPuesto. revisar idPuesto enviado");
        }


        // 2️⃣ Obtener los nombres de los  componentes que pertenecen al puesto anterior
        List<String> componentesEsperados = new ArrayList<>();
        for (FormulaPuesto fp : puestoAnterior){
            String componente = fp.getFormulaDetalle().getComponente().getNombre();
            componentesEsperados.add(componente);

        }
        //valida si el arreglo de componentes esperados esta vacio.
        if (componentesEsperados.isEmpty()) {
            throw  new IllegalArgumentException("No se encontraron componentes esperados del puesto anterior");

        }

        // 3️⃣ Tomar el último componente esperado (por orden en la fórmula)
        String ultimoComponenteEsperado = componentesEsperados.get(componentesEsperados.size() - 1);
        //validamos manualmente (por ahora) cual es el ultimo componente.
        System.out.println("ULTIMO COMPONENTE ESPERADO DEL PUESTO ANTERIOR:"+ ultimoComponenteEsperado);

        // 4️⃣ Buscar si ese componente fue agregado en ese puesto y producto
        boolean existe = detalleRepository
                .existsByEstadoAndPuestoAndComponente_Nombre(
                        2, //en proceso.
                        idpuestoAnterior,
                        ultimoComponenteEsperado
                );
        // 5️⃣ retornamos el resultado del booleano.
        return existe;
    }





    //proceso para consultar pendientes para puestos.

    public ValeLectura ConsultarPendientes ( String codigoProducto,Integer puestoActual,UsuarioDTO usuarioDTO) throws NoSuchFieldException {
        //variables locales.
        String CodigoProductofinal = "";
        ValeProduccion vale = new ValeProduccion();
        //buscar el ValeProduccion con estado = 2(Pendiente).
        Optional<ValeProduccion> primerVale = produccionRepository.findFirstByEstadoOrderByIdValeAsc(2);
        if (primerVale.isPresent()){
            vale = primerVale.get();

            //si ese vale produccion existe, busca en los produccionDetalle y consigue el serial del productofinal.
            CodigoProductofinal = vale.getSerial();


        }else {
            throw new NoSuchFieldException("no se encontro un vale en proceso para ese producto.");
        }
        //paso 2: obtener los datos de referencia del puesto anterior usando el valeProduccion anexado
        List<ValeProduccionDetalle> componentesAgregados = detalleRepository.findByValeProduccion(vale);
        List<ComponenteDTO> compDTO = new ArrayList<>();
        //creamos una lista de los componentes creados ( para llevar trazo de los componentes en el producto, ( no afecta a valedetalle
        for(ValeProduccionDetalle vd: componentesAgregados){
            if (vd.getEstado() == 2) {
                ComponenteDTO dto = new ComponenteDTO(vd.getComponente().getNombre(), vd.getComponente().getCodigo(), vd.getCantidad());
                compDTO.add(dto);
            }
        }

        //paso 3: anexar los componentes completados

        //generamos el nuevo valeLectura.
        ValeLectura nuevaLectura = new ValeLectura();
        nuevaLectura.setComponentes(compDTO);
        if(componentesAgregados.isEmpty()){
            throw new IllegalStateException("no hay componentes completos del puesto anterior para este producto");
        }

        nuevaLectura.setPuestoActual(puestoActual);
        nuevaLectura.setUsuario(usuarioDTO);
        nuevaLectura.setTiempo(LocalDateTime.now());
        nuevaLectura.setCodigoProducto(codigoProducto);
        nuevaLectura.setEstado("EN PROCESO");
        nuevaLectura.setIdTrazado(CodigoProductofinal);
        return nuevaLectura;

    }


}
