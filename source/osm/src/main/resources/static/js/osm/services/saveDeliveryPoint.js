export async function saveDeliveryPoint(point) {
    return {
        location: {
            lat: point.latitude,
            lng: point.longitude,
            address: point.address
        }
    };
}
