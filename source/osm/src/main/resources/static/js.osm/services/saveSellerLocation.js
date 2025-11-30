export async function saveSellerLocation(sellerId, location) {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');

    const token = tokenMeta ? tokenMeta.getAttribute('content') : null;
    const header = headerMeta ? headerMeta.getAttribute('content') : null;

    const headers = { 'Content-Type': 'application/json' };
    if (token && header) headers[header] = token;

    const payload = {
        location: {
            lat: location.lat,
            lng: location.lng,
            address: location.address
        }
    };

    console.log("Saving location for sellerId:", sellerId);
    console.log("Payload inviato (DTO):", JSON.stringify(payload, null, 2));

    const response = await fetch(`/sellerslocations/api/${sellerId}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify(payload)
    });

    if (!response.ok)
        throw new Error(`Failed to update seller location: ${response.status}`);
}
