import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const Drafts = ({ isActive }) => {
  const navigate = useNavigate();

  const handleDraftsClick = () => {
    navigate('/search/in%3Adrafts');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleDraftsClick}>
      <i className="bi bi-file-earmark-text"></i>
      <span>Drafts</span>
    </div>
  );
};

export default Drafts; 