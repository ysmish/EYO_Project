import { useContext, createContext, useState } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const loginAction = async (data) => {
    try {
        /**
         *       const response = await fetch("your-api-endpoint/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      }); // respose is a jwt token
        if (!response.ok) {
          throw new Error("Login failed");
        }
        const token = await response.text();
        setToken(token);
        localStorage.setItem("token", token);
        const decoded = verify(token, "your-secret-key");
        setUser(decoded);
        navigate("/");
         */
        // hardcoded user for demo purposes
        setToken("hardcoded-jwt-token");
        localStorage.setItem("token", "hardcoded-jwt-token");
        setUser({
          id: "12345",
          username: "demoUser"});
    } catch (err) {
      console.error(err);
    }
  };

  const logOut = () => {
    setUser(null);
    setToken("");
    localStorage.removeItem("token");
  };

  return (
    <AuthContext.Provider value={{ token, user, loginAction, logOut }}>
      {children}
    </AuthContext.Provider>
  );

};

export default AuthProvider;

export const useAuth = () => {
  return useContext(AuthContext);
};