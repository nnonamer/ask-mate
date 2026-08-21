import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';

export default function NewQuestion() {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [errorMsg, setErrorMsg] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth/login');
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setErrorMsg(null);

    try {
      const resp = await fetch(`${url}/question/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          title: title,
          description: description,
          userId: user.id
        })
      });

      if (resp.ok) {
        navigate('/');
      } else {
        setErrorMsg('Failed to post question. Please try again.');
      }
    } catch (err) {
      console.error('Error posting question:', err);
      setErrorMsg('An error occurred while submitting your question.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center my-4">
      <Card style={{ width: '100%', maxWidth: '650px' }} className="shadow-sm border-0 p-4">
        <Card.Body>
          <Card.Title className="fw-bold fs-3 mb-4 text-center">
            Ask a Question
          </Card.Title>

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formTitle">
              <Form.Label className="fw-semibold">Title</Form.Label>
              <Form.Control
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </Form.Group>

            <Form.Group className="mb-4" controlId="formDescription">
              <Form.Label className="fw-semibold">Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={6}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
              />
            </Form.Group>

            {errorMsg && (
              <p className="text-danger small mb-3">{errorMsg}</p>
            )}

            <div className="d-flex justify-content-end gap-2">
              <Button variant="secondary" onClick={() => navigate('/')} disabled={submitting}>
                Cancel
              </Button>
              <Button variant="primary" type="submit" className="px-4 fw-semibold" disabled={submitting}>
                {submitting ? 'Posting...' : 'Post Question'}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
}
