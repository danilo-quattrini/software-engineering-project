export async function fetchAddressFromCoordinates(lat, lng) {
    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`;

    const res = await fetch(url, {
        headers: {
            "Accept": "application/json",
            "User-Agent": "YourAppName/1.0" // Replace with your app name and version
        }
    });

    const data = await res.json();
    return data.display_name || "Indirizzo non trovato";
}