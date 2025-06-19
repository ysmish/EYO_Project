import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const Inbox = ({ isActive }) => {
  const navigate = useNavigate();

  const handleInboxClick = () => {
    navigate('/search/in%3Ainbox');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleInboxClick}>
      <i className="bi bi-inbox-fill"></i>
      <span>Inbox</span>
    </div>
  );
};

export default Inbox;