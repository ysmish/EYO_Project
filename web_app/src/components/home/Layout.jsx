import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import { useEffect, useState } from "react";
import Navbar from "./navbar/Navbar";
import Sidebar from "./sidebar/Sidebar";

const Layout = () => {
  const auth = useAuth();
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await fetch(`http://localhost:3000/api/users/${auth.user}`, {
          headers: {
            Authorization: `Bearer ${auth?.token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch user data");
        }

        const userData = await response.json();
        setUser(userData);
        // You can set user data in context or state if needed
      } catch (error) {
        console.error("Error fetching user data:", error);
        // Optionally handle the error, e.g., show a notification
      }
    };
    if (!auth?.token || !auth?.user) {
      auth.logOut(); // Clear auth state if token or user is not available
      navigate('/login');
    }
    fetchUser();
  }, [auth, navigate, setUser]);

  if (!auth.token) {
    return null; // Return null while redirecting
  }
  return (
    <>
      <Navbar user={user} setUser={setUser} searchQuery={searchQuery} setSearchQuery={setSearchQuery} />
      <div className="home-container">
        <Sidebar setSearchQuery={setSearchQuery} />
        <Outlet />
      </div>
    </>
  );
};

export default Layout;