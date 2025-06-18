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
      await new Promise(resolve => setTimeout(resolve, 100));
      
      if (!auth?.token) {
        navigate('/login');
        setIsCheckingAuth(false);
        return;
      }

      // Validate the token by making a test API call
      try {
        const response = await fetch("http://localhost:3000/api/mails", {
          headers: {
            "Authorization": `${auth.token}`
          }
        });

        if (!response.ok) {
          // Token is invalid or expired
          auth.logOut(); // This will clear both context and localStorage
          navigate('/login');
          setIsCheckingAuth(false);
          return;
        }

        // Token is valid
        setIsCheckingAuth(false);
      } catch (error) {
        console.error('Error validating token:', error);
        // On network error, still allow access but log the error
        setIsCheckingAuth(false);
      }
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