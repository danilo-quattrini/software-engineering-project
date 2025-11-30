import { fetchAddressFromCoordinates } from '/js/osm/helpers/fetchAddressFromCoordinates.js';

export function addEventLocationOnClick(mapComponent) {
    mapComponent.map.on('click', async (e) => {
        try {
            const { lat, lng } = e.latlng;
            const address = await fetchAddressFromCoordinates(lat, lng);

            // Aggiunge il marker sulla mappa
            L.marker([lat, lng]).addTo(mapComponent.map)
                .bindPopup(address)
                .openPopup();
            mapComponent.map.setView([lat, lng], 16);

            // Aggiorna i campi hidden nel form
            document.getElementById('lat').value = lat;
            document.getElementById('lng').value = lng;
            document.getElementById('locationName').value = address;

            // Temporaneamente ignoriamo il salvataggio remoto
            // await saveEventLocation({ latitude: lat, longitude: lng, address });
        } catch (err) {
            console.error("Error adding event location:", err);
        }
    });
}
