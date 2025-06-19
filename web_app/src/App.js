import "./styles.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/home/Layout";
import Mail from "./components/home/mail/Mail";
import Login from "./components/auth/login/Login";
import Register from "./components/auth/register/Register";
import { ThemeProvider } from "./context/ThemeProvider";
import AuthProvider from "./context/AuthProvider";
import SearchMails from "./components/home/mails/SearchMails";
import NotFound from "./components/notfound/NotFound";

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        
        <div className="App">
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<Navigate to="/search/in%3Ainbox" replace />} />
                <Route path="/search/:searchString/:mailId" element={<Mail />} />
                <Route path="/mails" element={<Navigate to="/search/in%3Aall" replace />} />
                <Route path="/search/:query" element={<SearchMails />} />
              </Route>
              <Route path="login" element={<Login />} />
              <Route path="register" element={<Register />} />
              {/* Catch-all route for 404 */}
              <Route path="*" element={<NotFound />} />
            </Routes>
          </BrowserRouter>
        </div>
      </AuthProvider>
    </ThemeProvider>
  );
}