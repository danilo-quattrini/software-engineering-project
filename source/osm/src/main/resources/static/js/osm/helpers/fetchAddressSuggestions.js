export async function fetchAddressSuggestions(query) {
    if (!query || query.length < 3) return [];

    const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&addressdetails=1&limit=5`;

    const res = await fetch(url, {
        headers: {
            "Accept": "application/json",
            "User-Agent": "YourAppName/1.0"
        }
    });

    return await res.json();
}