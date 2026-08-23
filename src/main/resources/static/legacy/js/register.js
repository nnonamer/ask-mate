document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('register-form');
    const message = document.getElementById('register-message');

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
            const response = await fetch('/api/user', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: username, password: password })
            });

            if (response.ok) {
                const userId = await response.json();
                message.style.color = 'green';
                message.textContent = 'Registration successful! Your user id is ' + userId + '.';
                form.reset();
            } else {
                message.style.color = 'red';
                message.textContent = 'Registration failed. Please try again.';
            }
        } catch (error) {
            message.style.color = 'red';
            message.textContent = 'Registration failed: could not reach the server.';
        }
    });
});
