import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const Spam = ({ isActive }) => {
  const navigate = useNavigate();

  const handleSpamClick = () => {
    navigate('/search/in%3Aspam');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleSpamClick}>
      <i className="bi bi-exclamation-triangle"></i>
      <span>Spam</span>
    </div>
  );
};

export default Spam; 