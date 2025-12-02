document.addEventListener("DOMContentLoaded", () => {
    const redirectUrl = document.getElementById("redirectUrl")?.value || "/psp/order/confirmed";

    try {
        if (window.opener && !window.opener.closed) {
            window.opener.location.assign(redirectUrl);
            window.close();
        } else {
            window.location.replace(redirectUrl);
        }
    } catch (e) {
        window.location.replace(redirectUrl);
    }
});
