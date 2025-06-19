import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const AllMails = ({ isActive }) => {
  const navigate = useNavigate();
  
  const handleAllMailsClick = () => {
    navigate('/search/in%3Aall');
  };

  return (
    <div className={`side-item ${isActive ? 'active' : ''}`} onClick={handleAllMailsClick}>
      <i className="bi bi-envelope-fill"></i>
      <span>All Mails</span>
    </div>
  );
};

export default AllMails; 