//variables globales
let currentUser = null;
let username = document.getElementById('username').value;
let password = document.getElementById('password').value;
let puesto = document.getElementById('puestoSelect').value;
const loginError = document.getElementById('loginError');

// Función para manejar el inicio de sesión
async function handleLogin() {
    
    const UsuarioLoginDTO = {
        puesto: document.getElementById('puestoSelect').value.trim(),
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value.trim()
    };
     try {
        
        console.log(UsuarioLoginDTO);
        // Simulación de llamada a la BD (reemplaza por tu API real)
        const response = await fetch('http://localhost:9090/api/usuario/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(  UsuarioLoginDTO)
        });
        const result = await response.json();
        console.log(result);
        // Verifica si el login fue exitoso
        if (result.username && result.id) {
            localStorage.clear();
            // Guarda los datos del usuario en localStorage
            localStorage.setItem('puesto', result.id);
            localStorage.setItem('nombre', result.nombre);
            localStorage.setItem('username', result.username);
            localStorage.setItem('currentUser', JSON.stringify(result));
            // Redirige y pasa datos por parámetros en la URL
            const params = new URLSearchParams({
                username: result.username,
                nombre: result.nombre,
                puesto: result.id
            });
            window.location.href = `http://localhost:9090/FRONT/HTML/Escaneo_linea.html?${params.toString()}`;
        } else {
            loginError.textContent = "Usuario o contraseña incorrectos.";
        }
    } catch (error) {
        loginError.textContent = "Error de conexión.";
    }
}

document.getElementById('loginForm').onsubmit = function(e) {
    e.preventDefault();
    const user = document.getElementById('username').value.trim();
    const pass = document.getElementById('password').value;
    const puesto = document.getElementById('puestoSelect').value;
    if (user && pass && puesto) {
        handleLogin();
    } else {
        loginError.textContent = "Por favor, complete todos los campos.";
    }
};
