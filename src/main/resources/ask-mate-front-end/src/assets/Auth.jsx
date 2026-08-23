import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';

export default function Auth() {
  const { type } = useParams();
  const isRegister = type === 'register';
  const { login } = useAuth();
  const navigate = useNavigate();
  const [warnMsg, setWarnMsg] = useState(null);

  const url = import.meta.env.VITE_SPRINGBOOT_BASE_URL;
  
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: ''
  });

  useEffect(() => {
    setFormData({
      username: '',
      password: '',
      confirmPassword: ''
    });
  }, [type]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (isRegister){
      if (formData.password === formData.confirmPassword){
        const resp = await fetch(`${url}/user`, {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ username: formData.username, password: formData.password })
        });

        if (resp.ok){
          const data = await resp.json();
          const userPayload = {id: typeof data === 'number' ? data : data.id, username: formData.username}
          login(userPayload);
          navigate('/');

        } else setWarnMsg("Error creating account");

      } else setWarnMsg("Passwords must match");
    } else {
      const resp = await fetch(`${url}/user/login`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ username: formData.username, password: formData.password })
      });

      if (resp.ok){
        const data = await resp.json();
        const userPayload = {id: data.id, username: formData.username}
        login(userPayload);
        navigate('/');

      } else setWarnMsg("Invalid credentials");
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center my-5">
      <Card style={{ width: '100%', maxWidth: '420px' }} className="shadow-sm border-0 p-3">
        <Card.Body>
          <Card.Title className="text-center fw-bold fs-3 mb-4">
            {isRegister ? 'Create an Account' : 'Welcome Back'}
          </Card.Title>

          <Form onSubmit={handleSubmit}>
              <Form.Group className="mb-3" controlId="formUsername">
                <Form.Label>Username</Form.Label>
                <Form.Control
                  type="text"
                  name="username"
                  placeholder="Enter username"
                  value={formData.username}
                  onChange={handleChange}
                  required
                />
              </Form.Group>

            <Form.Group className="mb-3" controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                name="password"
                placeholder="Password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </Form.Group>

            {isRegister && (
              <Form.Group className="mb-3" controlId="formConfirmPassword">
                <Form.Label>Confirm Password</Form.Label>
                <Form.Control
                  type="password"
                  name="confirmPassword"
                  placeholder="Confirm password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required
                />
              </Form.Group>
            )}

            <Button variant="primary" type="submit" className="w-100 mt-2 py-2 fw-semibold">
              {isRegister ? 'Register' : 'Login'}
            </Button>
          </Form>

          <div className="text-center mt-4">
            {isRegister ? (
              <small className="text-muted">
                Already have an account?{' '}
                <Link to="/auth/login" className="text-primary text-decoration-none fw-semibold">
                  Login here
                </Link>
              </small>
            ) : (
              <small className="text-muted">
                Don't have an account?{' '}
                <Link to="/auth/register" className="text-primary text-decoration-none fw-semibold">
                  Register here
                </Link>
              </small>
            )}
          </div>
          {warnMsg && (<p className='text-danger text-center'>{warnMsg}</p>)}
        </Card.Body>
      </Card>
    </Container>
  );
}