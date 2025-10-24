import { apiFetch } from './apiFetch.js';
import { inicializarServidor } from './servidor.js';





//variables globales
let currentUser = null;
let username = document.getElementById('username').value;
let password = document.getElementById('password').value;
let puesto = document.getElementById('puestoSelect').value;
const loginError = document.getElementById('loginError');

// Función para manejar el inicio de sesión
async function handleLogin() {
    await inicializarServidor(); // Asegura que el servidor esté inicializado

    const UsuarioLoginDTO = {
        puesto: document.getElementById('puestoSelect').value.trim(),
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value.trim()
    };
    try {
        console.log("🔐 Intentando login:", UsuarioLoginDTO);

        //usamos el apifetch.
        const result = await apiFetch("/api/usuario/login", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(UsuarioLoginDTO)
        });
        console.log("✅ Resultado del login:", result);

        // Verifica si el login fue exitoso
        if (result.username && result.id) {
            localStorage.clear();
            // Guarda los datos del usuario en localStorage
            localStorage.setItem('puesto', result.id);
            localStorage.setItem('nombre', result.nombre);
            localStorage.setItem('username', result.username);
            localStorage.setItem('currentUser', JSON.stringify(result));

            // Redirige a la página de escaneo de línea
            const params = new URLSearchParams({
                username: result.username,
                nombre: result.nombre,
                puesto: result.id
            });
            window.location.href = `${window.API_BASE_URL}/FRONT/HTML/Escaneo_linea.html?${params.toString()}`;
        } else {
            loginError.textContent = "Usuario o contraseña incorrectos.";
        }
    } catch (error) {
        console.error("❌ Error durante el login:", error);
        loginError.textContent ="Error de conexión con el servidor.";
    }
}
    
document.getElementById('loginForm').onsubmit = (e) => {
    e.preventDefault();
    const user = document.getElementById('username').value.trim();
    const pass = document.getElementById('password').value;
    const puesto = document.getElementById('puestoSelect').value;
    if (user && pass && puesto){
        
        handleLogin();
    } else {
        loginError.textContent = "Por favor, complete todos los campos.";
    }
}
