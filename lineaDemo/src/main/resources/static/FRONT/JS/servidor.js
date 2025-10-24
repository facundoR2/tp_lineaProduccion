// servidor.js
export async function detectarServidor() {
    // Para evitar loops eternos y errores de interpolación
    const tryFetch = async (url) => {
        try {
            const res = await fetch(`${url}/api/ip`, { method: "GET" });
            if (res.ok) {
                const data = await res.json();
                console.log(`✅ Servidor detectado en: ${data.url}`);
                localStorage.setItem("apiBaseUrl", data.url);
                window.API_BASE_URL = data.url;
                return data.url;
            }
        } catch (err) {
            // Ignoramos errores individuales para probar otras rutas
        }
        return null;
    };

    // 1️⃣ Intentar con window.location.origin (p. ej. http://192.168.0.8:9090)
    const originURL = window.location.origin;
    console.log("🔍 Probando con la URL de origen:", originURL);
    let url = await tryFetch(originURL);
    if (url) return url;

    // 2️⃣ Intentar con la URL cacheada
    const cachedURL = localStorage.getItem("apiBaseUrl");
    if (cachedURL) {
        console.log("📦 Probando con la URL en caché:", cachedURL);
        url = await tryFetch(cachedURL);
        if (url) return url;
    }

    // 3️⃣ Intentar escanear la red local (rango 192.168.0.x)
    console.log("🌐 Buscando servidor en la red local...");
    const baseRed = "192.168.0";
    for (let i = 1; i < 255; i++) {
        const testUrl = `http://${baseRed}.${i}:9090`;
        url = await tryFetch(testUrl);
        if (url) return url;
    }

    console.error("❌ No se pudo encontrar el servidor en la red local.");
    return null;
}

export async function inicializarServidor() {
    let url = localStorage.getItem("apiBaseUrl");

    if (!url) {
        console.log("🔎 Buscando servidor...");
        url = await detectarServidor();
    }

    if (url) {
        window.API_BASE_URL = url;
        console.log("✅ Servidor inicializado en:", url);
        return url;
    }

    console.error("⚠ No se pudo inicializar el servidor.");
    return null;
}