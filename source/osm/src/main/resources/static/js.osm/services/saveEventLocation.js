export async function saveEventLocation(point) {
    const res = await fetch("/eventslocations/api", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(point)
    });

    if (!res.ok) {
        throw new Error("Failed to save event location");
    }

    return await res.json();
}