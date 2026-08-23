import React, { useEffect, useState } from 'react'
import { Container, Form, Button, Card, Badge, Spinner } from 'react-bootstrap';
import {useParams, Link, useNavigate} from 'react-router-dom'
import { formatDate } from '../util';
import Answer from './components/Answer';
import { useAuth } from '../context/AuthContext';
 
function QuestionPage() {
  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;

  const { id } = useParams();
  const { user, isAuthenticated } = useAuth();
  const [question, setQuestion] = useState(null);
  const [answers, setAnswers] = useState(null);
  const [answerMessage, setAnswerMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState(null);

  const [askerName, setAskerName] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchQuestion() {
      const resp = await fetch(`${url}/question/${id}`);
      const data = await resp.json();
      setQuestion(data);

      if (data?.userId) {
        try {
          const userResp = await fetch(`${url}/user/${data.userId}`);
          const userData = await userResp.json();
          setAskerName(userData.username);
        } catch (err) {
          console.error('Error fetching asker info:', err);
        }
      }
    }

    async function fetchAnswers() {
      const resp = await fetch(`${url}/answer/${id}/all`);
      const data = await resp.json();
      setAnswers(data);
    }

    fetchQuestion();
    fetchAnswers();
  }, [id, url]);

  const handleAnswerSubmit = async (e) => {
    e.preventDefault();
    if (!answerMessage.trim()) return;

    setSubmitting(true);
    setErrorMsg(null);

    try {
      const resp = await fetch(`${url}/answer/`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          message: answerMessage,
          questionId: parseInt(id, 10),
          userId: user.id
        })
      });

      if (resp.ok) {
        setAnswerMessage('');
        const refreshedResp = await fetch(`${url}/answer/${id}/all`);
        const refreshedData = await refreshedResp.json();
        setAnswers(refreshedData);
      } else {
        setErrorMsg('Failed to post answer. Please try again.');
      }
    } catch (err) {
      console.error('Error submitting answer:', err);
      setErrorMsg('An error occurred while submitting your answer.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteQuestion = async () => {
    try {
      const resp = await fetch(`${url}/question/${question.id}`, {
        method: 'DELETE'
      });
      if (resp.ok) {
        navigate('/');
      }
    } catch (err) {
      console.error('Error deleting question:', err);
    }
  };

  const isQuestionOwner = user && user.id != null && question?.userId != null && String(user.id) === String(question.userId);

  return (
    question && answers ? ( 
      <Container className="py-2" style={{ maxWidth: '850px' }}>
        <Card className="shadow-sm border-0 mb-4 overflow-hidden">
          <Card.Header className="bg-primary text-white py-3 px-4 d-flex justify-content-between align-items-center">
            <Badge bg="light" text="dark" className="fs-6 px-3 py-2 rounded-pill">
              Question #{question.id}
            </Badge>
            {isQuestionOwner && (
              <Button variant="danger" size="sm" className="rounded-pill px-3" onClick={handleDeleteQuestion}>
                Delete Question
              </Button>
            )}
          </Card.Header>
          <Card.Body className="p-4">
            <h2 className="fw-bold mb-3">{question.title}</h2>
            <div className="text-muted small mb-4 pb-3 border-bottom d-flex align-items-center gap-2">
              <span>Asked by <strong className="text-dark">{askerName || 'User'}</strong> on <strong>{formatDate(question.created)}</strong></span>
            </div>
            <Card.Text className="fs-5 leading-relaxed text-secondary mb-0" style={{ whiteSpace: 'pre-wrap' }}>
              {question.description}
            </Card.Text>
          </Card.Body>
        </Card>

        <div className="mb-4">
          <h4 className="fw-bold mb-3 d-flex align-items-center gap-2">
            Answers <Badge bg="secondary" className="rounded-pill fs-6">{answers.length}</Badge>
          </h4>

          {answers.length === 0 ? (
            <Card className="text-center p-4 border-0 shadow-sm bg-light text-muted">
              <Card.Body>No answers yet.</Card.Body>
            </Card>
          ) : (
            answers.map(a => (
              <Answer 
                key={a.id} 
                answer={a} 
                onDelete={(deletedId) => setAnswers(prev => prev.filter(item => item.id !== deletedId))}
              />
            ))
          )}
        </div>

        <Card className="shadow-sm border-0 p-3">
          <Card.Body>
            <h5 className="fw-bold mb-3">Your Answer</h5>
            {isAuthenticated ? (
              <Form onSubmit={handleAnswerSubmit}>
                <Form.Group className="mb-3" controlId="answerMessage">
                  <Form.Control
                    as="textarea"
                    rows={4}
                    value={answerMessage}
                    onChange={(e) => setAnswerMessage(e.target.value)}
                    required
                    className="p-3"
                  />
                </Form.Group>
                {errorMsg && <p className="text-danger small mb-2">{errorMsg}</p>}
                <Button variant="primary" type="submit" disabled={submitting} className="px-4 py-2 fw-semibold rounded-pill">
                  {submitting ? (
                    <>
                      <Spinner animation="border" size="sm" className="me-2" />
                      Posting...
                    </>
                  ) : (
                    'Post Answer'
                  )}
                </Button>
              </Form>
            ) : (
              <div className="bg-light p-3 rounded text-center">
                <span className="text-muted">
                  Please{' '}
                  <Link to="/auth/login" className="text-primary text-decoration-none fw-semibold">
                    log in
                  </Link>{' '}
                  to post an answer.
                </span>
              </div>
            )}
          </Card.Body>
        </Card>
      </Container>
    ) : (
      <Container className="text-center py-5">
        <Spinner animation="border" variant="primary" className="mb-3" />
        <h4 className="text-muted">Loading question details...</h4>
      </Container>
    )
  )
}

export default QuestionPage