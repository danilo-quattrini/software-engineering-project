import { fetchAddressSuggestions } from '/js/osm/helpers/fetchAddressSuggestions.js';

/**
 * Creates a listener for address search input that fetches suggestions from Nominatim API.
 *
 * @param {HTMLInputElement} inputElement
 * @param {HTMLElement} suggestionBox
 * @param {(result: object) => void} onSelect callback when an address is selected
 */
export function createAddressSearchListener(inputElement, suggestionBox, onSelect) {
    let debounceTimer = null;

    inputElement.addEventListener('input', () => {
        const query = inputElement.value.trim();
        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(async () => {
            if (query.length < 3) {
                suggestionBox.innerHTML = "";
                suggestionBox.classList.add("hidden");
                return;
            }

            const results = await fetchAddressSuggestions(query);
            suggestionBox.innerHTML = "";

            if (results.length === 0) {
                suggestionBox.classList.add("hidden");
                return;
            }

            results.forEach(r => {
                const item = document.createElement('div');
                item.className =
                    "px-3 py-2 border-b border-gray-200 hover:bg-gray-100 cursor-pointer";
                item.textContent = r.display_name;

                item.addEventListener('click', () => {
                    suggestionBox.classList.add("hidden");
                    inputElement.value = r.display_name;
                    onSelect(r);
                });

                suggestionBox.appendChild(item);
            });

            suggestionBox.classList.remove("hidden");
        }, 300);
    });
}