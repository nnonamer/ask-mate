import React from 'react';
import { Navbar, Container, Button, Nav } from 'react-bootstrap';
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Layout() {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <>
      <Navbar bg="dark" variant="dark" expand={false} className="px-3 shadow-sm py-2">
        <Container fluid className="d-flex align-items-center justify-content-between">
          <Navbar.Brand as={Link} to="/" className="fw-bold fs-4 text-primary">
            AskMate
          </Navbar.Brand>
          <Nav className="d-flex flex-row align-items-center gap-2">
            {isAuthenticated ? (
              <>
                <span className="text-light me-2">
                  Hi, <strong>{user?.username || user?.email || 'User'}</strong>
                </span>
                <Button variant="outline-danger" size="sm" className="px-3 rounded-pill" onClick={logout}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button as={Link} to="/auth/login" variant="outline-light" size="sm" className="px-3 rounded-pill">
                  Login
                </Button>
                <Button as={Link} to="/auth/register" variant="primary" size="sm" className="px-3 rounded-pill fw-semibold">
                  Register
                </Button>
              </>
            )}
          </Nav>
        </Container>
      </Navbar>

      <main className="p-4">
        <Outlet />
      </main>
    </>
  );
}
