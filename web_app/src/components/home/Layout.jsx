import { Outlet, useNavigate, useParams, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import { useEffect, useState } from "react";
import Navbar from "./navbar/Navbar";
import Sidebar from "./sidebar/Sidebar";
import Loading from "../loading/Loading";

const Layout = () => {
  const auth = useAuth();
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearchLoading, setIsSearchLoading] = useState(false);
  const navigate = useNavigate();
  const params = useParams();
  const location = useLocation();

  // Handle search route loading and search query update
  useEffect(() => {
    const isSearchRoute = location.pathname.startsWith('/search/');
    
    if (isSearchRoute && params.query) {
      // Show loading state for search route
      setIsSearchLoading(true);
      
      // Safely decode the URL parameter with error handling
      let decodedQuery;
      try {
        decodedQuery = decodeURIComponent(params.query);
      } catch (error) {
        console.warn('Failed to decode URL parameter:', params.query, error);
        // Use the raw parameter if decoding fails
        decodedQuery = params.query;
      }
      
      // Update search query with the decoded parameter plus space
      const queryWithSpace = decodedQuery + " ";
      setSearchQuery(queryWithSpace);
      
      // Set a brief loading timeout to show the loading state
      // The actual loading will be handled by the SearchMails component
      const timer = setTimeout(() => {
        setIsSearchLoading(false);
      }, 500);
      
      return () => clearTimeout(timer);
    } else {
      setIsSearchLoading(false);
    }
  }, [location.pathname, params.query]);

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
        <Sidebar />
        {isSearchLoading ? <Loading /> : <Outlet />}
      </div>
    </>
  );
};

export default Layout;