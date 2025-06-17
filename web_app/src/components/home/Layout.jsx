import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import { useEffect, useState } from "react";
import Navbar from "./navbar/Navbar";
import Sidebar from "./sidebar/Sidebar";
import Loading from "../loading/Loading";

const Layout = () => {
  const auth = useAuth();
  const navigate = useNavigate();
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  useEffect(() => {
    // Check authentication state completely before proceeding
    const checkAuth = async () => {
      // Wait a moment to ensure auth context is fully loaded
      await new Promise(resolve => setTimeout(resolve, 800));
      
      if (!auth?.token) {
        navigate('/login');
      }
      setIsCheckingAuth(false);
    };

    checkAuth();
  }, [auth?.token, navigate]);

  if (isCheckingAuth) {
    return <Loading />;
  }

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