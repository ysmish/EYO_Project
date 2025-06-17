import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import { useEffect } from "react";
import Navbar from "./navbar/Navbar";
import Sidebar from "./sidebar/Sidebar";

const Layout = () => {
  const auth = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!auth?.token) {
      navigate('/login');
    }
  }, [auth?.token, navigate]);

  if (!auth?.token) {
    return null; // Return null while redirecting
  }

  return (
    <>
      <Navbar />
      <div className="home-container">
        <Sidebar />
        <Outlet />
      </div>
    </>
  );
};

export default Layout;