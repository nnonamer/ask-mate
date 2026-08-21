import './App.css'
import Home from './assets/Home'
import Layout from './assets/Layout'
import QuestionPage from './assets/QuestionPage'
import Auth from './assets/Auth'
import NewQuestion from './assets/NewQuestion'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        path: '/',
        element: <Home />
      },
      {
        path: '/question/:id',
        element: <QuestionPage />
      },
      {
        path: '/auth/:type',
        element: <Auth />
      },
      {
        path: '/new-question',
        element: <NewQuestion />
      }
    ]
  }
])

function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  )
}

export default App

