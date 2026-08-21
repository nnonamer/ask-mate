import React, { useEffect, useState } from 'react'
import { formatDate } from '../../util';
import { Card, CardTitle, Button, Badge } from 'react-bootstrap'; 
import { useAuth } from '../../context/AuthContext';

function Answer({ answer, onDelete }) {
  const [userName, setUserName] = useState(null); 
  const [points, setPoints] = useState(null);
  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;
  const { user } = useAuth();

  const isOwner = user && String(user.id) === String(answer.userId);

  useEffect(() => {
    async function fetchUserData() {
      try {
        const userResp = await fetch(`${url}/user/${answer.userId}`);
        const userData = await userResp.json();
        setUserName(userData.username);

        const pointsResp = await fetch(`${url}/user/${answer.userId}/points`);
        const pointsData = await pointsResp.json();
        setPoints(pointsData);
      } catch (err) {
        console.error('Error fetching user info or points:', err);
      }
    }

    fetchUserData();
  }, [answer.userId]);

  const handleDelete = async () => {
    try {
      const resp = await fetch(`${url}/answer/${answer.id}`, {
        method: 'DELETE'
      });
      if (resp.ok && onDelete) {
        onDelete(answer.id);
      }
    } catch (err) {
      console.error('Error deleting answer:', err);
    }
  };

  return (
    <Card className='my-2 p-3 border-0 shadow-sm'>
      <div className="d-flex justify-content-between align-items-center mb-1">
        <div className="d-flex align-items-center gap-2">
          <CardTitle className="mb-0 fw-bold">{userName || 'User'}</CardTitle>
          {points !== null && (
            <Badge bg="warning" text="dark" className="rounded-pill px-2 py-1">
              {points} pts
            </Badge>
          )}
        </div>
        {isOwner && (
          <Button variant="outline-danger" size="sm" className="rounded-pill px-3" onClick={handleDelete}>
            Delete
          </Button>
        )}
      </div>
      <p className='text-muted mb-2'>{formatDate(answer.submissionTime)}</p>
      <p className='mb-0'>{answer.message}</p>
    </Card>
  )
}

export default Answer