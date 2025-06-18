import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useNavigate } from 'react-router-dom';

const AllMails = ({setSearchQuery}) => {
  const navigate = useNavigate();
  
  const handleAllMailsClick = () => {
    setSearchQuery(''); // Clear the search bar
    navigate('/mails');
  };

  return (
    <div className='side-item' onClick={handleAllMailsClick}>
      <i className="bi bi-envelope-fill"></i>
      <span>All Mails</span>
    </div>
  );
};

export default AllMails; 