import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const Starred = ({ isActive }) => {
  const navigate = useNavigate();

  const handleStarredClick = () => {
    navigate('/search/in%3Astarred');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleStarredClick}>
      <i className="bi bi-star-fill"></i>
      <span>Starred</span>
    </div>
  );
};

export default Starred; 