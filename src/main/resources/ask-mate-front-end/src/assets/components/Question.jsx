import React, { useEffect, useState } from 'react'
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import Badge from 'react-bootstrap/Badge';
import { formatDate } from '../../util';
import { useNavigate } from 'react-router-dom';

function Question({ question }) {
  const navigate = useNavigate();
  const [askerName, setAskerName] = useState(null);
  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;

  useEffect(() => {
    async function fetchAsker() {
      if (!question?.userId) return;
      try {
        const resp = await fetch(`${url}/user/${question.userId}`);
        const data = await resp.json();
        setAskerName(data.username);
      } catch (err) {
        console.error('Error fetching asker info:', err);
      }
    }

    fetchAsker();
  }, [question?.userId, url]);

  return (
    <Card className='my-3 border-0 shadow-sm overflow-hidden hover-shadow transition-all'>
      <Card.Body className="p-4">
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title 
            className='fw-bold fs-4 text-dark mb-0'
            style={{ cursor: 'pointer' }}
            onClick={() => navigate(`/question/${question.id}`)}
          >
            {question.title}
          </Card.Title>
          <Badge bg="light" text="dark" className="border px-2 py-1">
            #{question.id}
          </Badge>
        </div>
        <Card.Subtitle className="text-muted small mb-3">
          Asked by <strong className="text-dark">{askerName || 'User'}</strong> on {formatDate(question.created)}
        </Card.Subtitle>
        <Card.Text className="text-secondary text-truncate-2 mb-3">
          {question.description}
        </Card.Text>
        <div className="d-flex justify-content-end">
          <Button 
            variant="outline-primary" 
            size="sm" 
            className="rounded-pill px-3 fw-semibold"
            onClick={() => navigate(`/question/${question.id}`)}
          >
            View Question & Answers &rarr;
          </Button>
        </div>
      </Card.Body>
    </Card>
  );
}

export default Question