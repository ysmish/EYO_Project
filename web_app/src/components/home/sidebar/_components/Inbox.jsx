import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Inbox = ({setSearchQuery}) => {
  const handleInboxClick = () => {
    setSearchQuery('in:inbox ');
  };

  return (
    <div className='side-item' onClick={handleInboxClick}>
      <i className="bi bi-inbox-fill"></i>
      <span>Inbox</span>
    </div>
  );
};

export default Inbox;