import { createAddressSearchListener } from '/js/osm/helpers/createAddressSearchListener.js';

export function addDeliveryPointByAddress(mapComponent, onSelect, state) {
    fetch('/templates/address-search.html')
        .then(r => r.text())
        .then(html => {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = html;

            const ui = wrapper.firstElementChild;
            const parent = mapComponent.map.getContainer().parentElement;

            parent.style.position = "relative";
            parent.appendChild(ui);

            const input = ui.querySelector('#address-input');
            const suggestions = ui.querySelector('#address-suggestions');

            createAddressSearchListener(input, suggestions, async (result) => {
                const lat = parseFloat(result.lat);
                const lng = parseFloat(result.lon);

                // Rimuovi marker precedente
                if (state.currentMarker) {
                    mapComponent.map.removeLayer(state.currentMarker);
                }

                // Crea nuovo marker
                const marker = L.marker([lat, lng]).addTo(mapComponent.map);
                marker.bindPopup(result.display_name).openPopup();
                mapComponent.map.setView([lat, lng], 16);

                // Aggiorna marker condiviso
                state.currentMarker = marker;

                onSelect({
                    latitude: lat,
                    longitude: lng,
                    address: result.display_name
                });
            });
        })
        .catch(err => console.error(err));
}
