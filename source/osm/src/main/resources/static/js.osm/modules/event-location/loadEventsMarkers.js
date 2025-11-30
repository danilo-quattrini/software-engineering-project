export async function loadEventsMarkers(mapComponent) {
    try {
        const response = await fetch('/api/eventslocations');
        const users = await response.json();

        users.forEach(u => {
            L.marker([u.lat, u.lng])
                .addTo(mapComponent.map)
                .bindPopup(`<b>${u.name}</b><br>${u.description}`);
        });
    } catch (error) {
        console.error("Error while loading events' markers:", error);
    }
}
