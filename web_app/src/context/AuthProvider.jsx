import { useContext, createContext, useState, useEffect } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(localStorage.getItem("user") || "");
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [validated, setValidated] = useState(false);
  const [isValidating, setIsValidating] = useState(false);

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
        setUser(data.username);
        localStorage.setItem("user", data.username);
        setValidated(true);
    } catch (err) {
      console.error(err);
      throw err;
    }
  };

  const logOut = () => {
    setUser(null);
    setToken("");
    setValidated(false);
    localStorage.removeItem("token");
    localStorage.removeItem("user");
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

  const validateToken = async (tokenToValidate) => {
    if (!tokenToValidate) {
      setValidated(false);
      return false;
    }

    try {
      setIsValidating(true);
      const response = await fetch("http://localhost:3000/api/tokens/validate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ token: tokenToValidate }),
      });

      if (response.ok) {
        setValidated(true);
        return true;
      } else {
        // Token is invalid, clear it
        console.warn("Token validation failed, clearing token");
        logOut();
        return false;
      }
    } catch (error) {
      console.error("Token validation error:", error);
      // On network error or other issues, clear the token to be safe
      logOut();
      return false;
    } finally {
      setIsValidating(false);
    }
  };

  // Automatically validate token on mount if it exists
  useEffect(() => {
    if (token && !validated) {
      validateToken(token);
    }
  }, [token, validated]);

  return (
    <AuthContext.Provider value={{ 
      token, 
      user, 
      validated, 
      isValidating, 
      loginAction, 
      logOut, 
      registerAction, 
      validateToken 
    }}>
      {children}
    </AuthContext.Provider>
  );

};

export default AuthProvider;

export const useAuth = () => {
  const context = useContext(AuthContext);
  
  // Automatically validate token when useAuth is called if not already validated
  useEffect(() => {
    if (context.token && !context.validated && !context.isValidating) {
      context.validateToken(context.token);
    }
  }, [context.token, context.validated, context.isValidating, context.validateToken]);
  
  return context;
};