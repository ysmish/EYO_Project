import "./styles.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/home/Layout";
import Mail from "./components/home/mail/Mail";
import Login from "./components/auth/login/Login";
import Register from "./components/auth/register/Register";
import { ThemeProvider } from "./context/ThemeProvider";
import AuthProvider from "./context/AuthProvider";
import DefaultMails from "./components/home/mails/DeafultMails";

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        
        <div className="App">
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<Navigate to="/mails" replace />} />
                <Route path="/mail/:mailId" element={<Mail />} />
                <Route path="/mails" element={<DefaultMails />} />
              </Route>
              <Route path="login" element={<Login />} />
              <Route path="register" element={<Register />} />
            </Routes>
          </BrowserRouter>
        </div>
      </AuthProvider>
    </ThemeProvider>
  );
}