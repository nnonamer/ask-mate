document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const message = document.getElementById('login-message');

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        message.textContent = '';
        message.style.color = '';

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        if (!username || !password) {
            message.textContent = 'Please fill in both username and password.';
            return;
        }

        try {
            const response = await fetch('/api/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: username, password: password })
            });

            if (response.ok) {
                const user = await response.json();
                sessionStorage.setItem('userId', user.id);
                sessionStorage.setItem('username', user.username);
                window.location.href = '/';
            } else {
                message.style.color = 'red';
                message.textContent = 'Invalid username or password.';
            }
        } catch (error) {
            message.style.color = 'red';
            message.textContent = 'Log in failed: could not reach the server.';
        }
    });
});
