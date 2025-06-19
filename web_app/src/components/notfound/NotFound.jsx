import { useNavigate } from 'react-router-dom';
import './NotFound.css';

const NotFound = () => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate('/search/in%3Ainbox');
  };

  return (
    <div className="notfound-container">
      <div className="notfound-error-code">
        404
      </div>
      
      <h1 className="notfound-title">
        Page Not Found
      </h1>
      
      <p className="notfound-message">
        Sorry, the page you are looking for doesn't exist or has been moved.
      </p>
      
      <button
        onClick={handleGoHome}
        className="notfound-button"
      >
        Go to Home
      </button>
    </div>
  );
};

export default NotFound; 