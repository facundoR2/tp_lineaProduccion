// Lógica para la página de escaneo de línea de producción
import { apiFetch } from './apiFetch.js';





window.listaComponentesArr = [];
window.procesoActual = 0;
window.procesos = [];

// Elementos del DOM

const codigoComponenteInput = document.getElementById('codigoComponente');
const mensaje = document.getElementById('mensaje');
const btnEscanear = document.getElementById('agregarComponente');
const btnLogout = document.getElementById('logoutButton');
const btnHelp = document.getElementById('helpButton');
// Variables para usuario




//generamos Un ValeLectura para trackear el progreso de cada Producto.

// estructuras del JSONs a enviar al backend.
const ValeLectura = {
    codigoproducto: "", //codigo serial del producto terminado.
    puestoActual: 0,
    tiempo: Date.now(),
    estado: "En Proceso", // "En Proceso", "Completado"
    componentes: [{
        componenteDTO: {
            nombre: "",
            codigo: "",  //codigo del producto(componente)
            cantidad: 0
        },
    }],
    usuario: "",

}
const componenteDTO = {
    nombre: "",
    codigo: "",  //codigo del producto(componente)
    cantidad: 0
}
const UsuarioDTO = {
    id: 0,
    username: "",
    nombre: ""
}
// ================
// Función para buscar la información principal desde la BD
//
async function buscarInfo() {
    let IdpuestoActual = localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0;
    try{
        //llamamos al apifetch.
        const data = await apiFetch('/api/produccion/info-general', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(IdpuestoActual),
        });

        // Actualiza la información en la página
        document.getElementById('codigoProducto').textContent = data.codigoProducto || 'N/A';
        localStorage.setItem('codigoProducto', data.codigoProducto || '');
        document.getElementById('ordenCarga').textContent = data.ordenProduccion || 'N/A';
        document.getElementById('counter_product').textContent = data.cantidadProduccion || 'N/A';
        window.codigoProducto = data.codigoProducto || '';
        window.userId = UsuarioDTO?.id || 0;
        document.getElementById('cantMaximaComponentes').textContent = `Máximo Componentes de la formula: ${data.cantMaxcompPuesto || 4}`;


        //guardamos la cantidad maxima de componentes en localstorage.
        localStorage.setItem('maxComponentes', data.cantMaxcompPuesto || 4);
        localStorage.setItem('cantidadActual', 0);
        // Consultar los procesos y pasos asociados al producto
        ConsultarComponentesPorPuesto(data.codigoProducto);

    }
    catch(error){
        console.error('Error al buscar la información principal:', error);
    }

    
}

async function ConsultarComponentesPorPuesto(codigoProducto){
    //consultamos la info sobre el producto a crear.

    if (!codigoProducto) {
        console.error('Código de producto no proporcionado.');
        return;
    }
    //mostramos en consola que se activo esta funcion.
    console.log("Consultando componentes para el producto:", codigoProducto);

    //hacemos fetch al endpoint.
    const id = localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0; 
    const InfoParaProcesoDTO = {
        codigoProducto: codigoProducto,
        idpuestoActual: id,
    };

    //llamamos al apifetch.
    try{
        const data = await apiFetch('/api/proceso/traer', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(InfoParaProcesoDTO),
        });

        if(!data){
            console.warn("No se recibieron datos de componentes para el proceso.");
            return;
        }
        console.log("respuesta obtenida",data);

        const ComponentesObtenidos = data.componentes || [];
        const ComponentesContainer = document.getElementById('ComponentesContainer');
        if(!ComponentesContainer){
            console.warn("No se encontró el contenedor de componentes en el html");
            return;
        }

        //limpiamos variables.
        ComponentesContainer.innerHTML = '';
        ComponentesObtenidos.forEach((Componente, componenteIndex) => {
            const ComponenteDiv = document.createElement('div');
            ComponenteDiv.className = `componente-azul componente-index-${componenteIndex}`;
            ComponenteDiv.innerHTML = `
                <h4>Componente ${componenteIndex + 1}: ${Componente.nombre || 'N/A'}</h4>
                <p><strong>Código:</strong> ${Componente.codigoProducto || 'N/A'}</p>
                <p><strong>Cantidad Requerida:</strong> ${Componente.cantidad || 'N/A'}</p>
            `;
            ComponentesContainer.appendChild(ComponenteDiv);
        });
    } catch(error){
        console.error('Error al consultar los componentes del proceso:', error);
    }

}
    






// Función para accionar la UI según el puesto del usuario
function accionarPorPuesto() {
    console.log("Puesto del usuario:", localStorage.getItem('puesto'));
    const puestostorage = parseInt(localStorage.getItem('puesto'), 10) || 0;
    const Elpuesto = window.puesto || 0;
    console.log("Puesto del usuario desde la URL:", Elpuesto);
    if (puestostorage == 1) {
        codigoComponenteInput.disabled = false;
        btnEscanear.disabled = false;
        crearValeLectura(UsuarioDTO);
    } else if (puestostorage == 0) {
        alert("El puesto tiene el valor 0. Por favor, seleccione un puesto válido.");
    } else {
        consultarPendientes();
    }
}



// Función para iniciar o reiniciar los procesos
function iniciarProcesos() {
    // --- Escaneo de componentes ---
    if (codigoComponenteInput) {
        codigoComponenteInput.value = '';
        codigoComponenteInput.focus();
    }
    const ul = document.getElementById('listaComponentes');
    if (ul){
        ul.innerHTML = '';
    }
    if (mensaje) {
        mensaje.textContent = '';
        mensaje.className = '';
    }
  
    
}
function actualizarInformacionVale(ValeLecturaCapturado) {
    if (!ValeLectura) return;
    ValeLectura.codigoproducto = ValeLecturaCapturado.codigoproducto || "";
    ValeLectura.puestoActual = ValeLecturaCapturado.puestoActual || 0;
    ValeLectura.tiempo = ValeLecturaCapturado.tiempo || Date.now();
    ValeLectura.estado = ValeLecturaCapturado.estado || "En Proceso";
    ValeLectura.componentes = ValeLecturaCapturado.componentes || [];
    ValeLectura.usuario = ValeLecturaCapturado.usuario || "";
}


async function crearValeLectura(UsuarioDTO) {
    console.log("Creando un nuevo vale de lectura para el puesto:", UsuarioDTO.id);
    //creamos un UsuarioDTO
    try{
        UsuarioDTO.id = parseInt(localStorage.getItem('puesto'), 10) || 0;
        UsuarioDTO.username = window.username || "invitado";
        UsuarioDTO.nombre = window.nombre || "Invitado";

        const valeLectura = await apiFetch(`/api/produccion/crear-Vale`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(UsuarioDTO)
        });

        // Guarda el valeLectura en una variable global o úsalo en la UI
        window.Lectura = valeLectura;
        console.log('Vale creado:', valeLectura);
        console.log('Código del Producto Final:', (valeLectura.codigoProducto));
        let codigoproductofinal = (valeLectura.codigoProducto) || "";
        localStorage.setItem('codigoProductoFinal', codigoproductofinal);
        window.codigoProductoFinal = valeLectura.codigoproducto || "";
        document.getElementById('codigoProductoFinal').textContent = valeLectura.codigoProducto || 'N/A';
        if (typeof actualizarInformacionVale === 'function') actualizarInformacionVale(valeLectura);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Vale creado correctamente.";
            mensaje.className = "success";
        }
    } catch (error) {
        console.error('Error al crear el vale:', error);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Error al crear el vale. Intente nuevamente.";
            mensaje.className = "error";
        }
    }
    
    
}

async function consultarPendientes() {
    console.log("Consultando pendientes para el puesto:", localStorage.getItem('puesto'));
    try{
        //generamos un payload.
        const ConsultarPendientesDTO ={
            codigoProducto: localStorage.getItem('codigoProducto') || "",
            idPuestoActual: localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0,
            usuarioDTO: UsuarioDTO,
        }
        //llamamos al apifetch.
        const valeLectura = await apiFetch(`/api/produccion/pendientes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ConsultarPendientesDTO)
        });
        
        switch (valeLectura.estado) {
            
            case "ESPERANDO_VALE_ANTERIOR":
                const mensajeEspera = document.getElementById('mensaje');
                mensajeEspera.textContent = "⏳ Esperando a que se complete el vale del puesto anterior.";
                mensajeEspera.className = "info";
                setTimeout(consultarPendientes, 5000); // Reintentar después de 5 segundos
                return;
                
            case "EN PROCESO":
                const mensajeEnProceso = document.getElementById('mensaje');
                mensajeEnProceso.textContent = "✅Encontrado Vale en proceso. Cargando Datos";
                mensajeEnProceso.className = "success";
                break;

            case "ERROR":
                const mensajeError = document.getElementById('mensaje');
                mensajeError.textContent = "❌ Error al consultar los pendientes. Intente nuevamente.";
                mensajeError.className = "error";
                return;
            default:
                const mensajeErrorDesconocido = document.getElementById('mensaje');
                mensajeErrorDesconocido.textContent = "❌ Estado desconocido del vale. Intente nuevamente.";
                mensajeErrorDesconocido.className = "error";
                break;
        }
        // Guarda el valeLectura en una variable global o úsalo en la UI
        window.Lectura = valeLectura;
        console.log('Vale creado:', valeLectura);
        console.log('Código del Producto Final:', (valeLectura.idTrazado));
        let codigoproductofinal = (valeLectura.idTrazado) || "";
        localStorage.setItem('codigoProductoFinal', codigoproductofinal);
        window.codigoProductoFinal = valeLectura.codigoproducto || "";
        document.getElementById('codigoProductoFinal').textContent = valeLectura.idTrazado || 'N/A';
        if (typeof actualizarInformacionVale === 'function') actualizarInformacionVale(valeLectura);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Vale cargado correctamente.";
            mensaje.className = "success";
        }
    } catch (error) {
        console.error('Error al el valeLectura.mensaje, :', error);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Error al consultar los pendientes. Intente nuevamente.";
            mensaje.className = "error";
        }
    }
}

// Función para manejar el escaneo de un componente

async function agregarComponenteAlGlobal() {
    const mensaje = document.getElementById('mensaje');
    const codigoComponenteInput = document.getElementById('codigoComponente');

    mensaje.textContent = "✅ Componente agregado correctamente.";
    mensaje.className = "success";

    codigoComponenteInput.value = "";
    codigoComponenteInput.focus();

    let cantidad_actual = parseInt(localStorage.getItem('cantidadActual') || '0', 10);
    cantidad_actual += 1;
    localStorage.setItem('cantidadActual', cantidad_actual);

    return cantidad_actual; // devuelve la nueva cantidad actual
}

export async function EscanearCodigo() {
    try {
        const codigoComponenteInput = document.getElementById('codigoComponente');
        const mensaje = document.getElementById('mensaje');
        const codigo = codigoComponenteInput.value.trim();

        if (!codigo) {
            mensaje.textContent = "⚠ Ingrese o escanee un código válido.";
            mensaje.className = "error";
            return false;
        }

        // 🔹 Paso 1: registrar componente en backend
        await escanearComponente(codigo); // esta función ya debe hacer el POST

        // 🔹 Paso 2: actualizar contador local y mensaje
        const cantidadActual = await agregarComponenteAlGlobal();

        // 🔹 Paso 3: obtener máximo
        const maxComponentes = parseInt(localStorage.getItem('maxComponentes') || '0', 10);

        mensaje.textContent = `✅ Componente ${codigo} registrado (${cantidadActual}/${maxComponentes})`;
        mensaje.className = "success";

        // 🔹 Paso 4: verificar si ya se completaron todos
        if (cantidadActual >= maxComponentes) {
            return true;
        }

        return false;
    } catch (error) {
        console.error("❌ Error en EscanearCodigo:", error);
        const mensaje = document.getElementById('mensaje');
        mensaje.textContent = "Error al registrar el componente.";
        mensaje.className = "error";
        return false;
    }
}


































    

async function obtenerNombreProductoPorCodigo(codigo) {
    try {
        const data =  await apiFetch(`/api/producto/nombre`, {
            method: 'POST',
            headers: { 'Content-Type': 'plain/text' },
            body: codigo,
        });
        return data.nombre || "Desconocido";

    }catch (error) {
        console.error('Error al buscar el nombre del producto:', error);
        return "";
    }
};
// funcionalidad para cada escaneo de componente.
async function escanearComponente(codigo) {
    console.log("Escaneando componente con código:", codigo);

    // Asegura que exista un <ul id="listaComponentes">; si no, lo crea y lo inserta en un lugar razonable
    let listaElement = document.getElementById('listaComponentes');
    if (!listaElement) {
        listaElement = document.createElement('ul');
        listaElement.id = 'listaComponentes';
        const contenedor = document.getElementById('scanned-list') || document.body.scanblock.scanned-list || document.body;
        contenedor.appendChild(listaElement);
    }
    
    //creamos uno nuevo
    const nombreProducto = await obtenerNombreProductoPorCodigo(codigo);
    const nuevoComponente = {
        nombre: nombreProducto,
        codigo: codigo,
        cantidad: 1,
    };

    
    const li = document.createElement('li');
    li.textContent = `Nombre:${nuevoComponente.nombre} (Código: ${nuevoComponente.codigo}) - Cantidad: ${nuevoComponente.cantidad}`;
    listaElement.appendChild(li);

    // crea el payload para enviar al backend.
    const payload = {
        codigoProducto: document.getElementById('codigoProductoFinal') ? document.getElementById('codigoProductoFinal').textContent : window.codigoProducto || '',
        codigoComponente: codigo,
        cantidadComponente: nuevoComponente.cantidad,
        puestoActual: parseInt(localStorage.getItem('puesto'), 10) || 0,
        usuario: {
            id: window.userId || 0,
            username: window.username || "invitado",
            nombre: window.nombre || "Invitado"
        }
    };

    try{
        const data = await apiFetch("/api/produccion/registrar-componente", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });
        console.log('Respuesta del servidor:', data);
        if(data.success){
            const mensaje = document.getElementById('mensaje');
            if (mensaje) {
                mensaje.textContent = data.message || "Componente registrado correctamente.";
                mensaje.className = "success";
            }
        } else {
            console.error('Error al registrar el componente nuevo:', data.message);
            const mensaje = document.getElementById('mensaje');
            if (mensaje) {
                mensaje.textContent = data.message || "Error al registrar el componente. Intente nuevamente.";
                mensaje.className = "error";
            }
        }

    }catch (error){
        console.error('Error al registrar el componente nuevo:', error);
        mensaje.textContent = "Error al registrar el componente. Intente nuevamente.";
        mensaje.className = "error";
    }
    let cantidad_actual = localStorage.getItem('cantidadActual') ? parseInt(localStorage.getItem('cantidadActual'), 10) : 0;
    let cantMaxcompPuesto = localStorage.getItem('maxComponentes') ? parseInt(localStorage.getItem('maxComponentes'), 10) : 4;
    if(cantidad_actual>= cantMaxcompPuesto){
        return true;
    }

}




async function obtenerDatosUsuarioDeURL() {
    const params = new URLSearchParams(window.location.search);
    //si no encontra parametros, los busca en el localstorage.
    if (!params.has('username') || !params.has('nombre') || !params.has('puesto')) {
        const storedUsername = localStorage.getItem('username');
        const storedNombre = localStorage.getItem('nombre');
        const storedPuesto = localStorage.getItem('puesto');
        params.set('username', storedUsername);
        params.set('nombre', storedNombre);
        params.set('puesto', storedPuesto);
    }
    const username = params.get('username') || 'invitado';
    const nombre = params.get('nombre') || 'Invitado';
    const id = localStorage.getItem('puesto') || 0;

    window.userInfo.username = username;
    window.userInfo.reloj = new Date().toLocaleTimeString();

    // Actualiza la UI con la información del usuario
    const userInfoDiv = document.getElementById('userInfo');
    if (userInfoDiv) {
        userInfoDiv.innerHTML = `Usuario: ${username} &nbsp; | &nbsp; ${window.userInfo.reloj}`;
    }
    const infoPuestoDiv = document.getElementById('info-puesto');
    if (infoPuestoDiv) {
        infoPuestoDiv.textContent = id ? `EST-${String(id).padStart(2, '0')}` : 'N/A';
    }
    // Actualiza el UsuarioDTO global
    UsuarioDTO.id = id;
    UsuarioDTO.username = username;
    UsuarioDTO.nombre = nombre;
    console.log('Datos del usuario obtenidos de la URL:', UsuarioDTO);
    return UsuarioDTO;
}

async function pasarAlsiguientePuesto() {
    console.log("Iniciando proceso para pasar al siguiente puesto...");
    const confirmacion = window.confirm("¿Desea pasar el producto al siguiente puesto?");
    if (!confirmacion) return;
    if (confirmacion) {
        try{
           
            let codigoProductoFinal = localStorage.getItem('codigoProductoFinal') || "";
            console.log("codigoproductofinal:", codigoProductoFinal);
            if (!codigoProductoFinal) {
                //si no lo consigue del main content, lo busca en la variable global.
                codigoProductoFinal = localStorage.getItem('codigoProductoFinal') || "";
            }
            if (!codigoProductoFinal) {
                alert("No se ha generado o encontrado el código del producto final. No se puede continuar.");
                return;
            }
            let puestoactual = localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0;
            if (puestoactual <= 0) {
                alert("El puesto actual no es válido. No se puede continuar.");
                return;
            }
            //creamos el payload.
            const payload = {
                codigoProducto: codigoProductoFinal,
                puestoActual: puestoactual,
                usuario: {
                    id: window.userId || 0,
                    username: window.userInfo.username || "invitado",
                    nombre: localStorage.getItem("nombre") || "Invitado"
                }
            };
            
            //continuamos con el fetch.
            const data = await apiFetch('/api/produccion/siguientePuesto',{
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            if(data.success){
                mensaje.textContent = data.message || "Producto pasado al siguiente puesto correctamente.";
                mensaje.className = "success";
                //limpiar storage y preparar nuevo ciclo.
                limpiarStorage();
                iniciarProcesos();
                accionarPorPuesto();
            }else{
                mensaje.textContent = data.message || "Error al pasar al siguiente puesto. Intente nuevamente.";
                mensaje.className = "error";
            }
        } catch (error) {
            console.error('Error al pasar al siguiente puesto:', error);
            mensaje.textContent = "Error al pasar al siguiente puesto. Intente nuevamente.";
            mensaje.className = "error";
        }
    } else {
        mensaje.textContent = "Acción de pasar al siguiente puesto cancelada.";
        mensaje.className = "info";
    }
};



// Eventos de botones
btnEscanear.addEventListener('click', async () => {
    const completo = await EscanearCodigo();

    // ✅ limpiar campo y mantener foco
    codigoComponenteInput.value = '';
    codigoComponenteInput.focus();

    if (completo) {
        const mensaje = document.getElementById('mensaje');
        mensaje.textContent = "🎉 Se han escaneado todos los componentes requeridos.";
        mensaje.className = "success";

        // Espera un poco antes de pasar al siguiente puesto
        setTimeout(() => {
            pasarAlsiguientePuesto();
        }, 1000);
    }
});

btnLogout.addEventListener('click', () => {
    // Limpiar el localStorage y redirigir al login o página principal
    localStorage.clear();
    window.location.href = 'login.html'; // Cambia esto a la URL de tu página de login
});
btnHelp.addEventListener('click', () => {
    // Mostrar una ventana emergente con instrucciones de ayuda
    alert("Instrucciones de Ayuda:\n\n1. Escanee los componentes necesarios utilizando el campo de entrada.\n2. Asegúrese de escanear la cantidad correcta de componentes según lo indicado.\n3. Una vez que todos los componentes estén escaneados, el sistema pasará automáticamente al siguiente puesto.\n4. Si necesita asistencia adicional, contacte al soporte técnico.");
});




function limpiarStorage(){
    // Limpiar los items relacionados al escaneo de componentes
    localStorage.removeItem('maxComponentes');
    localStorage.removeItem('cantidadActual');

}


export function inicializarEscaneo(){
    console.log("Inicializando lógica de escaneo de línea...");
    limpiarStorage();
    // --- Información principal ---
    obtenerDatosUsuarioDeURL(); // obtiene los datos del usuario desde la URL
    buscarInfo(); // busca la información principal desde la BD
    accionarPorPuesto(); // acciona la UI según el puesto del usuario

}