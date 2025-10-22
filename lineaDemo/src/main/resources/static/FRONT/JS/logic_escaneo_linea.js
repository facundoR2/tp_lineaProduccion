// Lógica para la página de escaneo de línea de producción


//Variables de parametro.
const urlParams = new URLSearchParams(window.location.search);
const username = urlParams.get('username');
if (username) {
    window.username = username;
}else{
    window.username = "invitado";
}
const puesto = urlParams.get('puesto');
if (puesto) {
    window.puesto = parseInt(puesto, 10);
}else{
    window.puesto = 0;
}



// Variables globales
let pasoActual = 0;
const pasos = [
    { nombre: "Escanear Componentes", descripcion: "Escanee todos los componentes necesarios." },
    { nombre: "Verificar Componentes", descripcion: "Verifique que todos los componentes estén correctos." },
    { nombre: "Finalizar Proceso", descripcion: "Confirme y finalice el proceso." }
];

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

//let userId = 0; // Valor por defecto
//let nombre = "Invitado"; // Valor por defecto

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

function ConsultarComponentesPorPuesto(codigoProducto){
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

    fetch('http://localhost:9090/api/proceso/traer',{
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(InfoParaProcesoDTO)
    })
    .then(response => response.json())
    .then(data => {
        if(data){
            console.log("respuesta obtenida",data);
            const ComponentesObtenidos = data.componentes || [];
            //guardamos el contenedor de los componentes.
            const ComponentesContainer = document.getElementById('ComponentesContainer');
            if (ComponentesContainer) {
                // Limpiamos el contenedor antes de agregar nuevos procesos
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
                    // Agregamos el componente al array global de Componentes
                    ComponentesContainer.appendChild(ComponenteDiv);
                });
            }
        }
    })
    .catch(error => {
        console.error('Error al consultar los componentes del proceso:', error);
    });


    // una vez conseguidos los datos. los procesamos y los pasamos a la funcion.

    //mandamos a una funcion para renderizar los procesos y llevarlos al html.

}


// Función para buscar la información principal desde la BD
function buscarInfo() {
    let IdpuestoActual = localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0;
    
    fetch('http://localhost:9090/api/produccion/info-general',{
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(IdpuestoActual),

    })
    .then(response => response.json())
    .then(data => {
        // Actualiza la información en la página
        document.getElementById('codigoProducto').textContent = data.codigoProducto || 'N/A';
        localStorage.setItem('codigoProducto', data.codigoProducto || '');
        document.getElementById('ordenCarga').textContent = data.ordenProduccion || 'N/A';
        document.getElementById('counter_product').textContent = data.cantidadProduccion || 'N/A';
        window.codigoProducto = data.codigoProducto || '';
        window.userId = UsuarioDTO.id || 0;
        document.getElementById('cantMaximaComponentes').textContent = `Máximo Componentes de la formula: ${data.cantMaxcompPuesto || 4}`;
        //agregamos la cantidad maxima de componentes un item en localstorage.
        localStorage.setItem('maxComponentes', data.cantMaxcompPuesto || 4);

        //generamos un item en el localstorage para la cantidad actual.
        localStorage.setItem('cantidadActual', 0);

        // Consultar los procesos y pasos asociados al producto
        ConsultarComponentesPorPuesto(data.codigoProducto);
    })
    .catch(error => {
        console.error('Error al buscar la información principal:', error);
    });
   

    
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


function crearValeLectura(UsuarioDTO) {
    //creamos un UsuarioDTO
    
    fetch(`http://localhost:9090/api/produccion/crear-Vale`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(UsuarioDTO)
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al crear el vale');
        return response.json();
    })
    .then(valeLectura => {
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
    })
    .catch(error => {
        console.error('Error al crear el vale:', error);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Error al crear el vale. Intente nuevamente.";
            mensaje.className = "error";
        }
    });
}

function consultarPendientes() {
    //generamos un payload.
    const ConsultarPendientesDTO ={
        codigoProducto: localStorage.getItem('codigoProducto') || "",
        idPuestoActual: localStorage.getItem('puesto') ? parseInt(localStorage.getItem('puesto'), 10) : 0,
        usuarioDTO: UsuarioDTO,
    }


    fetch(`http://localhost:9090/api/produccion/pendientes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(ConsultarPendientesDTO)
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al consultar pendientes');
        return response.json();
    })
    .then(valeLectura => {
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
    }).catch(error => {
        console.error('Error al crear el vale:', error);
        const mensaje = document.getElementById('mensaje');
        if (mensaje) {
            mensaje.textContent = "Error al crear el vale. Intente nuevamente.";
            mensaje.className = "error";
        }
    });
   
}

// Función para manejar el escaneo de un componente

async function EscanearCodigo() {
    //obtenemos el maximo de componentes a escanear.
    maxComponentes = localStorage.getItem('maxComponentes') ? parseInt(localStorage.getItem('maxComponentes'), 10) : 4;
    //obtenemos la cantidad actual de componentes escaneados.
    const cantidadActual = localStorage.getItem('cantidadActual') ? parseInt(localStorage.getItem('cantidadActual'), 10) : 0;
    if (cantidadActual >= maxComponentes) {
        //limpiamos el mensaje anterior.
        mensaje.textContent = " ";
        mensaje.textContent = "Ya se escanearon todos los componentes requeridos.";
        mensaje.className = "error";
        return;
    }
    const codigo = codigoComponenteInput.value
    if (!codigo) {
        mensaje.textContent = " ";
        mensaje.textContent = "Ingrese un código de componente.";
        mensaje.className = "error";
        return;
    }
    escanearComponente(codigo);
    agregarComponenteAlGlobal(codigo);
};

    

async function obtenerNombreProductoPorCodigo(codigo) {
    try {
        const response = await fetch(`http://localhost:9090/api/producto/nombre?codigo=${encodeURIComponent(codigo)}`,{
            method: 'GET',
        });
        if (!response.ok) throw new Error('No se pudo obtener el nombre del producto');
        return await response.text();
    }catch (error) {
        console.error('Error al buscar el nombre del producto:', error);
        return "";
    }
};
// funcionalidad para cada escaneo de componente.
async function escanearComponente(codigo) {
    // Asegura que exista un <ul id="listaComponentes">; si no, lo crea y lo inserta en un lugar razonable
    let listaElement = document.getElementById('listaComponentes');
    if (!listaElement) {
        listaElement = document.createElement('ul');
        listaElement.id = 'listaComponentes';
        const contenedor = document.getElementById('scanned-list') || document.body;
        contenedor.appendChild(listaElement);
    }
    window.listaComponentesArr = window.listaComponentesArr || [];
    // paso 1: verificamos si el componente ya existe en la lista.
    const existente = window.listaComponentesArr.find(c => c.codigo === codigo);
    if (existente) {
        // incrementa la cantidad localmente
        existente.cantidad = (existente.cantidad || 1) + 1;
        if (mensaje) {
            mensaje.textContent = "El componente ya está registrado. Se incrementó la cantidad.";
            mensaje.className = "info";
        }
        // actualizar UI inmediatamente
        actualizarLista();

        // actualizamos cantidadActual en storage
        let cantidad_actual = localStorage.getItem('cantidadActual') ? parseInt(localStorage.getItem('cantidadActual'), 10) : 0;
        cantidad_actual += 1;
        localStorage.setItem('cantidadActual', cantidad_actual);
        console.log("Cantidad actualizada en storage:", cantidad_actual);

        // continuar: igualmente enviamos al backend la nueva cantidad para que lo registre allí también
        // creamos el payload usando la cantidad actualizada
        const payloadExistente = {
            codigoProducto: document.getElementById('codigoProductoFinal') ? document.getElementById('codigoProductoFinal').textContent : window.codigoProducto || '',
            codigoComponente: codigo,
            cantidadComponente: existente.cantidad,
            puestoActual: parseInt(localStorage.getItem('puesto'), 10) || 0,
            usuario: {
                id: window.userId || 0,
                username: window.username || "invitado",
                nombre: window.nombre || "Invitado"
            }
        };

        fetch('http://localhost:9090/api/produccion/registrar-componente', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payloadExistente)
        })
        .then(response => {
            if (!response.ok) throw new Error('Error al registrar el componente');
            return response.text();
        })
        .then(data => {
            console.log('Respuesta del servidor:',data);
            if(data.includes('se ha registrado correctamente')){
                if (mensaje) {
                    mensaje.textContent = "Componente registrado correctamente";
                    mensaje.className = "success";
                }
            }else{
                throw new Error('Error desconocido al registrar el componente');
            }
            
        })
        .catch(error => {
            console.error('Error al registrar el componente (existente):', error);
            if (mensaje) {
                mensaje.textContent = "Error al registrar el componente. Intente nuevamente.";
                mensaje.className = "error";
            }
        });

        return; // ya se manejó el caso existente (fetch en curso)
    }

    const nombreProducto = await obtenerNombreProductoPorCodigo(codigo);
    const nuevoComponente = {
        nombre: nombreProducto,
        codigo: codigo,
        cantidad: 1,
    };

    // Añade a la UI
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

    // paso 2: enviamos la informacion al endpoint para que se registre en la BD.
    fetch('http://localhost:9090/api/produccion/registrar-componente', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al registrar el componente');
        return response.text();
    }).then(data => {
            console.log('Respuesta del servidor:',data);
            if(data.includes('se ha registrado correctamente el ValeProduccionDetalle:')){
                if (mensaje) {
                    mensaje.textContent = "Componente registrado correctamente";
                    mensaje.className = "success";
                }
            }else{
                throw new Error('Error desconocido al registrar el componente');
            }
            
        }
    )
    .catch(error => {
        console.error('Error al registrar el componente:', error);
        if (mensaje) {
            mensaje.textContent = "Error al registrar el componente. Intente nuevamente.";
            mensaje.className = "error";
        }
    });
}



function agregarComponenteAlGlobal() {
   // actualizarLista();
    mensaje.textContent = "Componente agregado correctamente.";
    mensaje.className = "success";
    codigoComponenteInput.value = "";
    codigoComponenteInput.focus();
    let cantidad_actual = localStorage.getItem('cantidadActual') ? parseInt(localStorage.getItem('cantidadActual'), 10) : 0;
    cantidad_actual += 1;
    localStorage.setItem('cantidadActual', cantidad_actual);
    
}


function obtenerDatosUsuarioDeURL() {
    const params = new URLSearchParams(window.location.search);
    //si no encontra parametros, los busca en el localstorage.
    if (!params.has('username') || !params.has('nombre') || !params.has('puesto')) {
        const storedUsername = localStorage.getItem('username') || 'invitado';
        const storedNombre = localStorage.getItem('nombre') || 'Invitado';
        const storedPuesto = localStorage.getItem('puesto') || 0;
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


function registrarComponenteEnBackend(codigoProducto, componente, puesto, usuarioDTO) {
    fetch(`http://localhost:9090/api/produccion/registrar-componente?codigoProducto=${encodeURIComponent(codigoProducto)}&puesto=${puesto}&usuarioDTO=${encodeURIComponent(JSON.stringify(usuarioDTO))}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(componente)
    })
    .then(response => {
        if(response.textContent.includes('se ha registrado correctamente el ValeProduccionDetalle:')){
            if (mensaje) {
                mensaje.textContent = "Componente registrado correctamente";
                mensaje.className = "success";
            }
        }else{
            throw new Error('Error desconocido al registrar el componente');
        }
        if (!response.ok) throw new Error('Error al registrar el componente');
        return response.text();
    });
}




function pasarAlsiguientePuesto() {
    const confirmacion = window.confirm("¿Desea pasar el producto al siguiente puesto?");
    if (!confirmacion) return;
    if (confirmacion) {
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

        const payload = {
            codigoProducto: codigoProductoFinal,
            puestoActual: puestoactual,
            codProd: window.codigoProducto || "",
        };




        // Envía el JSON con la información actual a la BD para pasar al siguiente puesto
        fetch('http://localhost:9090/api/produccion/siguientePuesto',{
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            return response.text();
        })
        .then(text => {
            // informar al usuario que se pasó al siguiente puesto
            alert("Producto enviado al siguiente puesto");
            console.log('Respuesta del servidor(texto):',text );
            if(text.includes('ha pasado al proximo puesto')){
                if (mensaje) {
                    mensaje.textContent = "Producto pasado al siguiente puesto correctamente.";
                    mensaje.className = "success";
                }
                //limpiar storage y preparar nuevo ciclo.
                limpiarStorage();
                iniciarProcesos();
                accionarPorPuesto();

            }
        })
        .catch(error => {
            console.error('Error al pasar al siguiente puesto:', error);
            const mensaje = document.getElementById('mensaje');
            if (mensaje) {
                mensaje.textContent = "Error al pasar al siguiente puesto. Intente nuevamente.";
                mensaje.className = "error";
            }
        });
    }else {
        alert("Operación cancelada. El producto permanece en el puesto actual.");
    }    
};



// Eventos de botones

btnEscanear.addEventListener('click', async () => {
    await EscanearCodigo(); // Espera a que termine el escaneo
    
    //validamos si es el ultimo componente.
    const cantidadActual = localStorage.getItem('cantidadActual') ? parseInt(localStorage.getItem('cantidadActual'), 10) : 0;
    maxComponentes = localStorage.getItem('maxComponentes') ? parseInt(localStorage.getItem('maxComponentes'), 10) : 4;
    
    if (cantidadActual >= maxComponentes) {
        mensaje.textContent = "Se han escaneado todos los componentes requeridos.";
        mensaje.className = "success";
        // Espera un momento antes de pasar al siguiente puesto
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


document.addEventListener('DOMContentLoaded', () => {
    limpiarStorage();
    // --- Información principal ---
    obtenerDatosUsuarioDeURL(); // obtiene los datos del usuario desde la URL
    buscarInfo(); // busca la información principal desde la BD
    accionarPorPuesto(); // acciona la UI según el puesto del usuario
    
});