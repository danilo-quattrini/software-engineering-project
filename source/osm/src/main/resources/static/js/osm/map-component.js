/**
 * @typedef {Object} MapOptions
 * @property {number[]} [center]
 * @property {number} [zoom]
 * @property {string} [tileUrl]
 */
class MapComponent {
    /**
     * @param {string} elementId
     * @param {MapOptions} [options]
     */
    constructor(elementId, options = {}) {
        this.map = L.map(elementId).setView(options.center || [0, 0], options.zoom || 2);

        L.tileLayer(options.tileUrl || 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(this.map);

        this.modules = [];
    }

    /**
     * Register a module (function) receiving the component instance as parameter.
     * @param {Function} module - The module function to register.
     *
     * E.g.: map.use(addDeliveryPointOnClick)
     */
    use(module) {
        if (typeof module === 'function') {
            module(this);
            this.modules.push(module.name);
        } else {
            console.warn('Invalid module:', module);
        }
    }
}

export default MapComponent;
