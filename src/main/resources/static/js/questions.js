document.addEventListener('DOMContentLoaded', async () => {
    const questionsContainer = document.getElementById('questions');

    if (!questionsContainer) {
        return;
    }

    questionsContainer.textContent = 'Loading questions...';

    try {
        const response = await fetch('/api/question/all');

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const questions = await response.json();

        if (!Array.isArray(questions) || questions.length === 0) {
            questionsContainer.textContent = 'No questions found.';
            return;
        }

        questionsContainer.replaceChildren(
            ...questions.map((question) => {
                const article = document.createElement('article');

                const title = document.createElement('h2');
                title.textContent = question.title;

                const description = document.createElement('p');
                description.textContent = question.description;

                const meta = document.createElement('small');
                meta.textContent = `Created: ${new Date(question.created).toLocaleString()}`;

                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Delete';
                deleteButton.type = 'button';
                deleteButton.addEventListener('click', async () => {

                    try {
                        const response = await fetch(`/api/question/${question.id}`, {
                            method: 'DELETE'
                        })

                        if (!response.ok) {
                            console.error('Failed to delete question');
                            return;
                        }

                        console.log(question.id + " deleted");
                        window.location.reload()
                    } catch (e) {
                        console.error(e.message());
                    }
                })

                article.append(title, description, meta, deleteButton);
                return article;
            })
        );
    } catch (error) {
        questionsContainer.textContent = 'Could not load questions.';
    }
});
