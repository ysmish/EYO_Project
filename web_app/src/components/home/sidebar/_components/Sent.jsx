import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const Sent = ({ isActive }) => {
  const navigate = useNavigate();

  const handleSentClick = () => {
    navigate('/search/in%3Asent');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleSentClick}>
      <i className="bi bi-send-fill"></i>
      <span>Sent</span>
    </div>
  );
};

export default Sent; 