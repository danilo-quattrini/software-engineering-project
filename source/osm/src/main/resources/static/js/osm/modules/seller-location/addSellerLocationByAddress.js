import { saveSellerLocation } from '/js/osm/services/saveSellerLocation.js';
import { createAddressSearchListener } from '/js/osm/helpers/createAddressSearchListener.js';

export function addSellerLocationByAddress(mapComponent, sellerId) {
    if (!sellerId) {
        console.warn("addSellerLocationByAddress: nessun sellerId presente");
        return;
    }

    if (!mapComponent.sellerMarkers) {
        mapComponent.sellerMarkers = new Map();
    }

    const map = mapComponent.map;
    const parent = map.getContainer().parentElement;
    const mapElement = document.getElementById(map._container.id);
    const sellerName = mapElement.dataset.sellerName || "Venditore";
    const sellerEmail = mapElement.dataset.sellerEmail || "";

    fetch('/templates/address-search.html')
        .then(r => r.text())
        .then(html => {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = html;

            const ui = wrapper.firstElementChild;
            parent.style.position = "relative";
            parent.appendChild(ui);

            const input = ui.querySelector('#address-input');
            const suggestions = ui.querySelector('#address-suggestions');

            createAddressSearchListener(input, suggestions, async (result) => {
                const lat = parseFloat(result.lat);
                const lng = parseFloat(result.lon);
                const address = result.display_name;

                console.log("INDIRIZZO SELEZIONATO →", address, lat, lng);

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

                map.setView([lat, lng], 16);

                try {
                    await saveSellerLocation(sellerId, { lat, lng, address });
                    console.log("Posizione (da indirizzo) salvata con successo");
                } catch (err) {
                    console.error("Errore nel salvataggio della posizione:", err);
                }
            });
        })
        .catch(err => console.error(err));
}
