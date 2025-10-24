import { inicializarServidor } from "./servidor.js";

export async function apiFetch(endpoint, options = {}) {
    const base = window.API_BASE_URL 
        || localStorage.getItem('apiBaseUrl') 
        || await inicializarServidor() 
        || 'http://localhost:9090';

    if (!base) throw new Error('NO se detectó el servidor backend.');

    const url = `${base}${endpoint.startsWith("/") ? endpoint : "/" + endpoint}`;
    console.log(`🌐 API Fetch a: ${url}`);
    const response = await fetch(url, options);

    if (!response.ok) {
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    return response.json();
}