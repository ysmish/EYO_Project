import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Starred = ({setSearchQuery}) => {
  const handleStarredClick = () => {
    setSearchQuery('in:starred ');
  };

  return (
    <div className='side-item' onClick={handleStarredClick}>
      <i className="bi bi-star-fill"></i>
      <span>Starred</span>
    </div>
  );
};

export default Starred; 