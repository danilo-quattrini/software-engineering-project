import { fetchAddressFromCoordinates } from '/js/osm/helpers/fetchAddressFromCoordinates.js';
import { saveSellerLocation } from '/js/osm/services/saveSellerLocation.js';

export function addSellerLocationOnClick(mapComponent, sellerId) {
    if (!sellerId) {
        console.warn("addSellerLocationOnClick: nessun sellerId presente");
        return;
    }

    if (!mapComponent.sellerMarkers) {
        mapComponent.sellerMarkers = new Map();
    }

    const map = mapComponent.map;
    const mapElement = document.getElementById(map._container.id);
    const sellerName = mapElement.dataset.sellerName || "Venditore";
    const sellerEmail = mapElement.dataset.sellerEmail || "";

    map.on("click", async (e) => {
        const { lat, lng } = e.latlng;
        console.log("CLICK →", { sellerId, lat, lng });

        const address = await fetchAddressFromCoordinates(lat, lng);

        let marker = mapComponent.sellerMarkers.get(sellerId);
        if (marker) {
            marker.setLatLng([lat, lng]);
        } else {
            marker = L.marker([lat, lng]).addTo(map);
            mapComponent.sellerMarkers.set(sellerId, marker);
        }

        marker.bindPopup(
            `<b>${sellerName}</b><br>${sellerEmail}<br>${address}`
        ).openPopup();

        try {
            await saveSellerLocation(sellerId, { lat, lng, address });
            console.log("Posizione salvata con successo");
        } catch (err) {
            console.error("Errore nel salvataggio della posizione:", err);
        }
    });
}
