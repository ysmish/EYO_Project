import { Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import Navbar from "./navbar/Navbar";
import Sidebar from "./sidebar/Sidebar";

const Layout = () => {
  const auth = useAuth();
  if (!auth?.token) {
    return (
      <div className="home-container">
        <p onClick={auth?.loginAction} className="logout">
          You are not logged in
        </p>
      </div>
    )
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