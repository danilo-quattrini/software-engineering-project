import { createAddressSearchListener } from '/js/osm/helpers/createAddressSearchListener.js';

export function addEventLocationByAddress(mapComponent) {
    fetch('/templates/address-search.html')
        .then(r => r.text())
        .then(html => {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = html;
            const ui = wrapper.firstElementChild;

            const mapContainer = mapComponent.map.getContainer();
            const parent = mapContainer.parentElement;
            parent.style.position = "relative";
            parent.appendChild(ui);

            const input = ui.querySelector('#address-input');
            const suggestions = ui.querySelector('#address-suggestions');

            createAddressSearchListener(input, suggestions, (result) => {
                const lat = parseFloat(result.lat);
                const lng = parseFloat(result.lon);
                const address = result.display_name;

                // Aggiunge marker e centra la mappa
                L.marker([lat, lng]).addTo(mapComponent.map)
                    .bindPopup(address)
                    .openPopup();
                mapComponent.map.setView([lat, lng], 16);

                // Aggiorna hidden fields
                document.getElementById('lat').value = lat;
                document.getElementById('lng').value = lng;
                document.getElementById('locationName').value = address;

                // Temporaneamente ignoriamo il salvataggio remoto
                // saveEventLocation({ latitude: lat, longitude: lng, address });
            });
        })
        .catch(err => console.error(err));
}
