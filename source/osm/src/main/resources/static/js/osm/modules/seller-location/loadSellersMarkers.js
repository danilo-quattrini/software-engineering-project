export async function loadSellersMarkers(mapComponent) {
    try {
        const response = await fetch('/sellerslocations/api/sellers');
        const sellers = await response.json();

        sellers.forEach(s => {
            if (s.location && s.location.lat != null && s.location.lng != null) {
                L.marker([s.location.lat, s.location.lng])
                    .addTo(mapComponent.map)
                    .bindPopup(`<b>${s.name}</b><br>${s.email}`);
            }
        });

    } catch (error) {
        console.error("Error while loading sellers' markers:", error);
    }
}
