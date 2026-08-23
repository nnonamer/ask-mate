document.addEventListener("DOMContentLoaded", () => {
    const logoutButton = document.getElementById("logout-button");

    if (!logoutButton) {
        return;
    }

    logoutButton.addEventListener("click", async () => {
        try {
            const response = await fetch('/api/user/logout', {
                method: 'POST'
            })

            if (response.ok) {
                sessionStorage.removeItem('userId');
                sessionStorage.removeItem('username');
                window.location.href = '/';
            } else {
                console.error('Logout failed');
            }
        } catch (e) {
            console.error(e.message());
        }
    });
});