import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Sent = ({setSearchQuery}) => {
  const handleSentClick = () => {
    setSearchQuery('in:sent ');
  };

  return (
    <div className='side-item' onClick={handleSentClick}>
      <i className="bi bi-send-fill"></i>
      <span>Sent</span>
    </div>
  );
};

export default Sent; 