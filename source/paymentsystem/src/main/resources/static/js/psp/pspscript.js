const btn = document.querySelector("#pay-btn");

btn.addEventListener("click", () => {

    const isEvent = document.querySelector("#isEvent")?.value === "true";

    const referenceIdEl = document.querySelector("#referenceId");
    const payerEmailEl = document.querySelector("#payerEmail");

    const dto = {
        referenceId: referenceIdEl?.value || null,
        payerEmail: payerEmailEl?.value || null
    };

    if (!isEvent) {
        dto.location = {
            lat: parseFloat(document.querySelector("#lat")?.value || null),
            lng: parseFloat(document.querySelector("#lng")?.value || null),
            address: document.querySelector("#address")?.value || null
        };
    }

    window.location.href = "/psp/pspdemo?dto=" + encodeURIComponent(JSON.stringify(dto));
});
