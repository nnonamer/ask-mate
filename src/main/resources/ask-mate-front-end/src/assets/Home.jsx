import React, { useEffect, useState } from 'react'
import Question from './components/Question';
import { Container, FormControl, Button, Row, Col, Spinner, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;
  const { isAuthenticated } = useAuth();
  const [questions, setQuestions] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');


  useEffect(() => {
    async function fetchQuestions() {
      if (!searchTerm.trim()){
        const resp = await fetch(`${url}/question/all`);
        const data = await resp.json();
        setQuestions(data);
      }
      else {
        const resp = await fetch(`${url}/question/search/${searchTerm}`);
        const data = await resp.json();
        setQuestions(data);
      }
    }

    fetchQuestions();
  }, [searchTerm]);

  return (
    <Container className='py-3' style={{ maxWidth: '850px' }}>
      <Card className="shadow-sm border-0 mb-4 p-3 bg-light">
        <Row className='align-items-center g-3'>
          <Col xs={12} sm={8} md={9}>
            <FormControl
              type="search"
              placeholder="🔍 Search questions by keyword..."
              className="rounded-pill border-0 shadow-sm px-4 py-2"
              aria-label="Search"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </Col>
          <Col xs={12} sm={4} md={3} className="text-end">
            <Button
              as={Link}
              to={isAuthenticated ? "/new-question" : "/auth/login"}
              variant="primary"
              className="w-100 rounded-pill px-3 py-2 fw-semibold shadow-sm"
            >
              + Ask Question
            </Button>
          </Col>
        </Row>
      </Card>
      
      {/* Questions Feed */}
      {questions ? (
        questions.length === 0 ? (
          <Card className="text-center p-5 border-0 shadow-sm bg-white">
            <Card.Body>
              <h5 className="text-muted mb-3">No questions found</h5>
              <p className="text-secondary small mb-4">
                {searchTerm ? `No questions matched "${searchTerm}". Try a different keyword.` : "Be the first person to ask a question!"}
              </p>
              <Button
                as={Link}
                to={isAuthenticated ? "/new-question" : "/auth/login"}
                variant="outline-primary"
                className="rounded-pill px-4"
              >
                Ask a Question
              </Button>
            </Card.Body>
          </Card>
        ) : (
          questions.map(q => (
            <Question key={q.id} question={q} />
          ))
        )
      ) : (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" className="mb-3" />
          <h5 className="text-muted">Loading questions...</h5>
        </div>
      )}
    </Container>
  )
}

export default Home