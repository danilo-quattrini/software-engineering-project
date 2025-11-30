import { fetchAddressFromCoordinates } from '/js/osm/helpers/fetchAddressFromCoordinates.js';

export function addDeliveryPointOnClick(mapComponent, onSelect) {
    mapComponent.map.on('click', async (e) => {
        try {
            const { lat, lng } = e.latlng;

            const address = await fetchAddressFromCoordinates(lat, lng);

            const marker = L.marker(e.latlng).addTo(mapComponent.map);
            marker.bindPopup(address).openPopup();

            onSelect({
                latitude: lat,
                longitude: lng,
                address: address
            });

        } catch (err) {
            console.error("Error adding delivery point:", err);
        }
    });
}
