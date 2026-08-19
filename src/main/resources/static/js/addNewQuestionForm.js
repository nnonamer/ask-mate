document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('add-question');

    if (!container) {
        return;
    }

    const form = document.createElement('form');
    form.id = 'add-question-form';

    const titleLabel = document.createElement('label');
    titleLabel.htmlFor = 'question-title';
    titleLabel.textContent = 'Title';

    const titleInput = document.createElement('input');
    titleInput.type = 'text';
    titleInput.id = 'question-title';
    titleInput.name = 'title';
    titleInput.required = true;

    const descriptionLabel = document.createElement('label');
    descriptionLabel.htmlFor = 'question-description';
    descriptionLabel.textContent = 'Description';

    const descriptionInput = document.createElement('textarea');
    descriptionInput.id = 'question-description';
    descriptionInput.name = 'description';
    descriptionInput.rows = 5;
    descriptionInput.required = true;

    const userIdInput = document.createElement('input');
    userIdInput.type = 'hidden';
    userIdInput.id = 'question-user-id';
    userIdInput.name = 'userId';
    userIdInput.value = sessionStorage.getItem('userId') || '';

    const submitButton = document.createElement('button');
    submitButton.type = 'submit';
    submitButton.textContent = 'Add question';

    const message = document.createElement('p');
    message.id = 'add-question-message';

    form.append(
        titleLabel,
        titleInput,
        descriptionLabel,
        descriptionInput,
        userIdInput,
        submitButton,
        message
    );

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        message.textContent = '';
        message.style.color = '';

        const userId = Number(userIdInput.value);

        if (!userId) {
            message.style.color = 'red';
            message.textContent = 'You must be logged in to add a question.';
            return;
        }

        const payload = {
            title: titleInput.value.trim(),
            description: descriptionInput.value.trim(),
            userId
        };

        if (!payload.title || !payload.description) {
            message.style.color = 'red';
            message.textContent = 'Please fill in both title and description.';
            return;
        }

        try {
            const response = await fetch('/api/question/', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error(`Request failed with status ${response.status}`);
            }

            form.reset();
            userIdInput.value = sessionStorage.getItem('userId') || '';
            message.style.color = 'green';
            message.textContent = 'Question added.';
            window.location.reload();
        } catch (error) {
            message.style.color = 'red';
            message.textContent = 'Could not add question.';
            console.error(error.message());
        }
    });

    container.replaceChildren(form);
});
