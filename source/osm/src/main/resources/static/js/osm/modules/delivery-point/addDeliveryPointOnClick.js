import { fetchAddressFromCoordinates } from '/js/osm/helpers/fetchAddressFromCoordinates.js';

export function addDeliveryPointOnClick(mapComponent, onSelect, state) {
    mapComponent.map.on('click', async (e) => {
        try {
            const { lat, lng } = e.latlng;
            const address = await fetchAddressFromCoordinates(lat, lng);

            if (state.currentMarker) {
                mapComponent.map.removeLayer(state.currentMarker);
            }

            const marker = L.marker(e.latlng).addTo(mapComponent.map);
            marker.bindPopup(address).openPopup();

            state.currentMarker = marker;

            onSelect({
                latitude: lat,
                longitude: lng,
                address
            });

        } catch (err) {
            console.error("Error adding delivery point:", err);
        }
    });
}
