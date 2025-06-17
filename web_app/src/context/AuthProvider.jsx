import { useContext, createContext, useState } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const loginAction = async (data) => {
    try {
        const response = await fetch("http://localhost:3000/api/tokens", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
      
        const result = await response.json();
        
        if (!response.ok) {
          throw new Error(result.error || "Login failed");
        }
        
        setToken(result.token);
        localStorage.setItem("token", result.token);
        setUser({
          username: data.username
        });
    } catch (err) {
      console.error(err);
      throw err;
    }
  };

  const logOut = () => {
    setUser(null);
    setToken("");
    localStorage.removeItem("token");
  };

  const registerAction = async (data) => {
    try {
      const response = await fetch("http://localhost:3000/api/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || errorData.message || "Registration failed");
      }
      
      return { success: true };
    } catch (err) {
      console.error("Registration error:", err);
      throw err;
    }
  };

  return (
    <AuthContext.Provider value={{ token, user, loginAction, logOut, registerAction }}>
      {children}
    </AuthContext.Provider>
  );

};

export default AuthProvider;

export const useAuth = () => {
  return useContext(AuthContext);
};